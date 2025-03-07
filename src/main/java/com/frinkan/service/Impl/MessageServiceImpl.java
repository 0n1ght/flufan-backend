package com.frinkan.service.Impl;

import com.frinkan.dto.MessageDto;
import com.frinkan.entity.Account;
import com.frinkan.entity.Message;
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

    public void sendMessage(Long receiverId, String content) {
        Account sender = accountService.getAuthenticatedAccount();
        Account receiver = accountService.getById(receiverId);

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        messageRepo.save(message);
    }

    public List<MessageDto> getConversation(Long userId) {
        Account currentUser = accountService.getAuthenticatedAccount(); // Pobierz obecnego użytkownika
        List<Message> messages = messageRepo.findConversation(currentUser.getId(), userId); // Pobierz wiadomości

        // Mapowanie listy wiadomości na listę DTO
        List<MessageDto> messageDtos = messages.stream().map(message -> {
            MessageDto messageDto = new MessageDto();
            messageDto.setId(message.getId());
            messageDto.setSender(accountMapper.toAccountDto(message.getSender())); // Mapuj nadawcę na AccountDto
            messageDto.setReceiver(accountMapper.toAccountDto(message.getReceiver())); // Mapuj odbiorcę na AccountDto
            messageDto.setContent(message.getContent());
            messageDto.setSentAt(message.getSentAt());
            messageDto.setReadStatus(message.isReadStatus());
            return messageDto;
        }).collect(Collectors.toList());

        return messageDtos;
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setReadStatus(true);
        messageRepo.save(message);
    }
}
