package com.frinkan.service;

import com.frinkan.dto.MessageDto;
import com.frinkan.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    void sendMessage(Long receiverId, String content, MessageType messageType);
    Page<MessageDto> getConversation(Long userId, Pageable pageable);
    void markAsRead(Long messageId);
    boolean wasConversation(long user1Id, long user2Id);
}
