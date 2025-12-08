package com.flufan.dto;

import com.flufan.enums.MessageType;

import java.util.Date;

public class MessageDto {
    private Long id;
    private AccountDto sender;
    private AccountDto receiver;
    private String content;
    private boolean readStatus;
    private Date sentAt;
    private MessageType messageType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountDto getSender() {
        return sender;
    }

    public void setSender(AccountDto sender) {
        this.sender = sender;
    }

    public AccountDto getReceiver() {
        return receiver;
    }

    public void setReceiver(AccountDto receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
