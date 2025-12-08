package com.flufan.controller;

import com.flufan.dto.MessageDto;
import com.flufan.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<String> sendMessage(@PathVariable Long receiverId,
                                              @RequestBody MessageDto messageDto) {
        messageService.sendMessage(receiverId, messageDto.getContent(), messageDto.getMessageType());
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Page<MessageDto>> getConversation(@PathVariable Long userId, Pageable pageable) {
        Page<MessageDto> conversation = messageService.getConversation(userId, pageable);
        return ResponseEntity.ok(conversation);
    }

    @PutMapping("/read/{messageId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok("Message marked as read");
    }
}
