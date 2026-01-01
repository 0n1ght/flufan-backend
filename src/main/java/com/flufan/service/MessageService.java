package com.flufan.service;

import com.flufan.dto.MessageDto;
import com.flufan.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface MessageService {
    void sendMessage(Long receiverId, String content, MessageType messageType);
    Page<MessageDto> getConversation(Long userId, Pageable pageable);
    int markAsRead(Instant date, Long receiverId);
    boolean wasConversation(long senderId, long receiverId);
}
