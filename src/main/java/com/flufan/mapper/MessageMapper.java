package com.flufan.mapper;

import com.flufan.dto.MessageDto;
import com.flufan.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    private final AccountMapper accountMapper;

    public MessageMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public MessageDto toMessageDto(Message message) {
        MessageDto messageDto = new MessageDto();

        messageDto.setId(message.getId());
        messageDto.setMessageType(message.getMessageType());
        messageDto.setContent(message.getContent());
        messageDto.setSender(accountMapper.toAccountDto(message.getSender()));
        messageDto.setReceiver(accountMapper.toAccountDto(message.getReceiver()));
        messageDto.setReadStatus(message.isReadStatus());
        messageDto.setSentAt(message.getSentAt());

        return messageDto;
    }
}
