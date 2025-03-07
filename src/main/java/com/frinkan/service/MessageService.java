package com.frinkan.service;

import com.frinkan.dto.MessageDto;

import java.util.List;

public interface MessageService {
    public void sendMessage(Long receiverId, String content);
    public List<MessageDto> getConversation(Long userId);
    public void markAsRead(Long messageId);
}
