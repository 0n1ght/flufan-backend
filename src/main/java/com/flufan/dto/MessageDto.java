package com.flufan.dto;

import com.flufan.enums.MessageType;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

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
