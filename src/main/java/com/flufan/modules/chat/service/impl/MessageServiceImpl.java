package com.flufan.modules.chat.service.impl;

import com.flufan.modules.chat.dto.MessageDto;
import com.flufan.modules.user.entity.Account;
import com.flufan.modules.chat.entity.Message;
import com.flufan.modules.chat.enums.MessageType;
import com.flufan.modules.notification.enums.NotificationType;
import com.flufan.common.exception.InsufficientMessagesException;
import com.flufan.common.exception.MessageDoesNotExist;
import com.flufan.common.exception.MessageLengthException;
import com.flufan.common.mapper.MessageMapper;
import com.flufan.common.model.Notification;
import com.flufan.modules.chat.repo.MessageRepo;
import com.flufan.modules.user.service.AccountService;
import com.flufan.modules.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final AccountService accountService;
    private final MessageMapper messageMapper;

    @Override
    public void sendMessage(UUID receiverPublicId, String content, MessageType messageType) {
        if (content.isEmpty() || (messageType == MessageType.TEXT && content.length() > 5000)) {
            throw new MessageLengthException("Message has wrong length");
        }
        if (messageType.equals(MessageType.BOUGHT_SERVICE)) {
            throw new MessageDoesNotExist("Wrong message");
        }

        Account sender = accountService.getAuthenticatedAccount();
        Account receiver = accountService.findByPublicId(receiverPublicId);

        long availableMessages = sender.getAvailableReplies().getOrDefault(receiverPublicId, 0L);
        if (availableMessages < 1) {
            throw new InsufficientMessagesException("You can't send any messages to this receiver yet");
        }

        sender.getAvailableReplies().put(receiver.getPublicId(), availableMessages - 1);
        accountService.updateAccount(sender);

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setSentAt(Instant.now());
        messageRepo.save(message);

        Notification notification = new Notification();
        notification.setType(NotificationType.NEW_MESSAGE);
        notification.setContent(sender.getUsername()+": "+content);
        receiver.getNotifications().add(notification);
        accountService.updateAccount(receiver);
    }

    @Override
    public Page<MessageDto> getConversation(UUID userPublicId, Pageable pageable) {
        Account currentUser = accountService.getAuthenticatedAccount();
        Page<Message> messagesPage = messageRepo.findConversation(currentUser.getPublicId(), userPublicId, pageable);
        return messagesPage.map(messageMapper::toMessageDto);
    }


    @Override
    public int markAsRead(Instant date, UUID receiverPublicId) {
        UUID senderPublicId = accountService.getAuthenticatedAccount().getPublicId();
        return messageRepo.markAsReadUpTo(senderPublicId, receiverPublicId, date);
    }

    @Override
    public boolean wasConversation(UUID senderPublicId, UUID receiverPublicId) {
        List<Message> messages = messageRepo.findConversation(senderPublicId, receiverPublicId, Pageable.unpaged()).getContent();

        boolean hasUser1ToUser2 = messages.stream()
                .anyMatch(m ->
                        m.getSender().getPublicId() == senderPublicId &&
                                m.getReceiver().getPublicId() == receiverPublicId);

        boolean hasUser2ToUser1 = messages.stream()
                .anyMatch(m ->
                        m.getSender().getPublicId() == receiverPublicId &&
                                m.getReceiver().getPublicId() == senderPublicId);

        return hasUser1ToUser2 && hasUser2ToUser1;
    }
}
