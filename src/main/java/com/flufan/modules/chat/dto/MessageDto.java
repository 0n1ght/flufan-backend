package com.flufan.modules.chat.dto;

import com.flufan.modules.chat.enums.MessageType;
import com.flufan.modules.user.dto.AccountDto;
import lombok.Data;

import java.time.Instant;

@Data
public class MessageDto {
    private Long id;
    private AccountDto sender;
    private AccountDto receiver;
    private String content;
    private boolean readStatus;
    private Instant sentAt;
    private MessageType messageType;
}
