package com.frinkan.service.impl;

import com.frinkan.dto.AccountDto;
import com.frinkan.dto.MessageDto;
import com.frinkan.entity.Account;
import com.frinkan.entity.Message;
import com.frinkan.enums.MessageType;
import com.frinkan.exception.InsufficientMessagesException;
import com.frinkan.exception.MessageLengthException;
import com.frinkan.mapper.AccountMapper;
import com.frinkan.repo.MessageRepo;
import com.frinkan.service.AccountService;
import com.frinkan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final AccountService accountService;
    private final AccountMapper accountMapper = new AccountMapper();

    @Autowired
    public MessageServiceImpl(MessageRepo messageRepo, AccountService accountService) {
        this.messageRepo = messageRepo;
        this.accountService = accountService;
    }

    public void sendMessage(Long receiverId, String content, MessageType messageType) {
        if (content.isEmpty() || content.length() > 400) {
            throw new MessageLengthException("Message has wrong length");
        }

        Account sender = accountService.getAuthenticatedAccount();

        long availableMessages = sender.getAvailableMessages().getOrDefault(receiverId, 0L);
        if (availableMessages < 1) {
            throw new InsufficientMessagesException("You can't send any messages to this receiver yet");
        }

        sender.getAvailableMessages().put(receiverId, availableMessages - 1);
        accountService.updateAccount(sender);

        Account receiver = accountService.getById(receiverId);
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setMessageType(messageType);
        messageRepo.save(message);

        receiver.setNotifications(receiver.getNotifications()+1);
        accountService.updateAccount(receiver);
    }


    public List<MessageDto> getConversation(Long userId) {
        Account currentUser = accountService.getAuthenticatedAccount(); // Pobierz obecnego użytkownika
        List<Message> messages = messageRepo.findConversation(currentUser.getId(), userId); // Pobierz wiadomości

        return messages.stream().map(message -> {
            AccountDto sender = accountMapper.toAccountDto(message.getSender());
            sender.setEmail("");
            AccountDto receiver = accountMapper.toAccountDto(message.getReceiver());
            receiver.setEmail("");
            MessageDto messageDto = new MessageDto();
            messageDto.setId(message.getId());
            messageDto.setSender(sender);
            messageDto.setReceiver(receiver);
            messageDto.setContent(message.getContent());
            messageDto.setSentAt(message.getSentAt());
            messageDto.setReadStatus(message.isReadStatus());
            messageDto.setMessageType(message.getMessageType());
            return messageDto;
        }).collect(Collectors.toList());
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setReadStatus(true);
        messageRepo.save(message);
    }
}
