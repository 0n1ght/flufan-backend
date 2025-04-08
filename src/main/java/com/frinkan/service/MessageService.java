package com.frinkan.service;

import com.frinkan.dto.MessageDto;
import com.frinkan.enums.MessageType;

import java.util.List;

public interface MessageService {
    void sendMessage(Long receiverId, String content, MessageType messageType);
    List<MessageDto> getConversation(Long userId);
    void markAsRead(Long messageId);
}
