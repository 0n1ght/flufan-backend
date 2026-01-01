package com.flufan.service;

import com.flufan.dto.MessageDto;
import com.flufan.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface MessageService {
    void sendMessage(UUID receiverPublicId, String content, MessageType messageType);
    Page<MessageDto> getConversation(UUID userPublicId, Pageable pageable);
    int markAsRead(Instant date, UUID receiverPublicId);
    boolean wasConversation(UUID senderPublicId, UUID receiverPublicId);
}
