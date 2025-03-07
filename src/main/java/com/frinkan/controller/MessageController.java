package com.frinkan.controller;

import com.frinkan.dto.MessageDto;
import com.frinkan.entity.Message;
import com.frinkan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send/{receiverId}")
    public ResponseEntity<String> sendMessage(@PathVariable Long receiverId, @RequestBody String content) {
        messageService.sendMessage(receiverId, content);
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageDto>> getConversation(@PathVariable Long userId) {
        List<MessageDto> conversation = messageService.getConversation(userId);
        return ResponseEntity.ok(conversation);
    }

    @PutMapping("/read/{messageId}")
    public ResponseEntity<String> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok("Message marked as read");
    }
}
