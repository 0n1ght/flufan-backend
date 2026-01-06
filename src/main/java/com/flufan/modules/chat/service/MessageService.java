package com.flufan.modules.chat.service;

import com.flufan.modules.chat.dto.MessageDto;
import com.flufan.modules.chat.enums.MessageType;
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
