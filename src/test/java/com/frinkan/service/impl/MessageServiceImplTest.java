package com.frinkan.service.impl;

import com.frinkan.dto.MessageDto;
import com.frinkan.entity.Account;
import com.frinkan.entity.Message;
import com.frinkan.enums.MessageType;
import com.frinkan.enums.NotificationType;
import com.frinkan.exception.InsufficientMessagesException;
import com.frinkan.exception.MessageDoesNotExist;
import com.frinkan.exception.MessageLengthException;
import com.frinkan.mapper.MessageMapper;
import com.frinkan.model.Notification;
import com.frinkan.repo.MessageRepo;
import com.frinkan.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    void testMarkAsRead_Success() {
        Message message = new Message();
        when(messageRepo.findById(1L)).thenReturn(Optional.of(message));

        messageService.markAsRead(1L);

        verify(messageRepo).save(message);
        assertTrue(message.isReadStatus());
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
