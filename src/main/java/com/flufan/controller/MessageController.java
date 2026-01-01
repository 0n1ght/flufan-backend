package com.flufan.controller;

import com.flufan.dto.MessageDto;
import com.flufan.dto.ReadMessageDto;
import com.flufan.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<String> sendMessage(@PathVariable UUID receiverPublicId,
                                              @RequestBody MessageDto messageDto) {
        messageService.sendMessage(receiverPublicId, messageDto.getContent(), messageDto.getMessageType());
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Page<MessageDto>> getConversation(@PathVariable UUID userPublicId, Pageable pageable) {
        Page<MessageDto> conversation = messageService.getConversation(userPublicId, pageable);
        return ResponseEntity.ok(conversation);
    }

    @PutMapping("/read-conversation")
    public ResponseEntity<String> markAsRead(@RequestBody ReadMessageDto readMessageDto) {
        int messagesMarkedAsRead = messageService.markAsRead(readMessageDto.date(), readMessageDto.receiverPublicId());
        return ResponseEntity.ok(messagesMarkedAsRead + "messages marked as read");
    }
}
