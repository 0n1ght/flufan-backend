package com.flufan.service.impl;

import com.flufan.entity.Account;
import com.flufan.entity.Message;
import com.flufan.enums.MessageType;
import com.flufan.exception.InsufficientMessagesException;
import com.flufan.exception.MessageDoesNotExist;
import com.flufan.exception.MessageLengthException;
import com.flufan.mapper.MessageMapper;
import com.flufan.repo.MessageRepo;
import com.flufan.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {
    @Mock
    private MessageRepo messageRepo;
    @Mock
    private AccountService accountService;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessage_EmptyContent_Throws() {
        Account sender = new Account();
        sender.setAvailableReplies(Map.of(2L, 1L));
        when(accountService.getAuthenticatedAccount()).thenReturn(sender);

        assertThrows(MessageLengthException.class,
                () -> messageService.sendMessage(2L, "", MessageType.TEXT));
    }

    @Test
    void testSendMessage_BoughtService_Throws() {
        Account sender = new Account();
        sender.setAvailableReplies(Map.of(2L, 1L));
        when(accountService.getAuthenticatedAccount()).thenReturn(sender);

        assertThrows(MessageDoesNotExist.class,
                () -> messageService.sendMessage(2L, "Any", MessageType.BOUGHT_SERVICE));
    }

    @Test
    void testSendMessage_InsufficientMessages_Throws() {
        Account sender = new Account();
        sender.setAvailableReplies(Map.of(2L, 0L));
        when(accountService.getAuthenticatedAccount()).thenReturn(sender);

        assertThrows(InsufficientMessagesException.class,
                () -> messageService.sendMessage(2L, "Hello", MessageType.TEXT));
    }

    @Test
    void testMarkAsReadById_Success() {
        Instant limit = Instant.now();
        Long senderId = 1L;
        Long receiverId = 2L;

        Account sender = new Account();
        sender.setId(senderId);

        when(accountService.getAuthenticatedAccount()).thenReturn(sender);
        when(messageRepo.markAsReadUpTo(senderId, receiverId, limit)).thenReturn(5);

        int updatedCount = messageService.markAsRead(limit, receiverId);

        assertEquals(5, updatedCount);
        verify(messageRepo).markAsReadUpTo(senderId, receiverId, limit);
        verify(accountService).getAuthenticatedAccount();
    }

    @Test
    void testWasConversation_ReturnsTrue() {
        Account user1 = new Account();
        user1.setId(1L);
        Account user2 = new Account();
        user2.setId(2L);

        Message m1 = new Message(); m1.setSender(user1); m1.setReceiver(user2);
        Message m2 = new Message(); m2.setSender(user2); m2.setReceiver(user1);

        Page<Message> page = new PageImpl<>(List.of(m1, m2));
        when(messageRepo.findConversation(1L, 2L, Pageable.unpaged())).thenReturn(page);

        assertTrue(messageService.wasConversation(1L, 2L));
    }

    @Test
    void testWasConversation_ReturnsFalse() {
        Account user1 = new Account();
        user1.setId(1L);
        Account user2 = new Account();
        user2.setId(2L);

        Message m1 = new Message(); m1.setSender(user1); m1.setReceiver(user2);

        Page<Message> page = new PageImpl<>(List.of(m1));
        when(messageRepo.findConversation(1L, 2L, Pageable.unpaged())).thenReturn(page);

        assertFalse(messageService.wasConversation(1L, 2L));
    }
}
