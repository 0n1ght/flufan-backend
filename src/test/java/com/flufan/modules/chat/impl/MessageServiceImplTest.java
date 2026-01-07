package com.flufan.modules.chat.impl;

import com.flufan.modules.chat.service.impl.MessageServiceImpl;
import com.flufan.modules.chat.dto.MessageDto;
import com.flufan.modules.user.entity.Account;
import com.flufan.modules.chat.entity.Message;
import com.flufan.modules.chat.enums.MessageType;
import com.flufan.common.exception.InsufficientMessagesException;
import com.flufan.common.exception.MessageDoesNotExist;
import com.flufan.common.exception.MessageLengthException;
import com.flufan.modules.chat.mapper.MessageMapper;
import com.flufan.modules.chat.repo.MessageRepo;
import com.flufan.modules.user.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepo messageRepo;

    @Mock
    private AccountService accountService;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void sendMessage_success() {
        UUID senderPublicId = UUID.randomUUID();
        UUID receiverPublicId = UUID.randomUUID();

        Account sender = new Account();
        sender.setPublicId(senderPublicId);
        sender.setUsername("sender");
        sender.setAvailableReplies(new java.util.HashMap<>());
        sender.getAvailableReplies().put(receiverPublicId, 1L);

        Account receiver = new Account();
        receiver.setPublicId(receiverPublicId);
        receiver.setNotifications(new java.util.ArrayList<>());

        when(accountService.getAuthenticatedAccount()).thenReturn(sender);
        when(accountService.findByPublicId(receiverPublicId)).thenReturn(receiver);

        messageService.sendMessage(receiverPublicId, "hello", MessageType.TEXT);

        assertEquals(0L, sender.getAvailableReplies().get(receiverPublicId));

        verify(messageRepo).save(argThat(msg ->
                msg.getSender().equals(sender) &&
                        msg.getReceiver().equals(receiver) &&
                        msg.getContent().equals("hello") &&
                        msg.getMessageType() == MessageType.TEXT
        ));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountService, times(2)).updateAccount(captor.capture());

        List<Account> updatedAccounts = captor.getAllValues();
        assertTrue(updatedAccounts.contains(sender));
        assertTrue(updatedAccounts.contains(receiver));

        assertEquals(1, receiver.getNotifications().size());
        assertEquals("sender: hello", receiver.getNotifications().get(0).getContent());
    }

    @Test
    void sendMessage_emptyContent_throwsException() {
        assertThrows(
                MessageLengthException.class,
                () -> messageService.sendMessage(UUID.randomUUID(), "", MessageType.TEXT)
        );
    }

    @Test
    void sendMessage_textTooLong_throwsException() {
        String content = "a".repeat(5001);

        assertThrows(
                MessageLengthException.class,
                () -> messageService.sendMessage(UUID.randomUUID(), content, MessageType.TEXT)
        );
    }

    @Test
    void sendMessage_wrongMessageType_throwsException() {
        assertThrows(
                MessageDoesNotExist.class,
                () -> messageService.sendMessage(UUID.randomUUID(), "x", MessageType.BOUGHT_SERVICE)
        );
    }

    @Test
    void sendMessage_noAvailableReplies_throwsException() {
        UUID receiverPublicId = UUID.randomUUID();

        Account sender = new Account();
        sender.setPublicId(UUID.randomUUID());

        when(accountService.getAuthenticatedAccount()).thenReturn(sender);
        when(accountService.findByPublicId(receiverPublicId)).thenReturn(new Account());

        assertThrows(
                InsufficientMessagesException.class,
                () -> messageService.sendMessage(receiverPublicId, "hi", MessageType.TEXT)
        );
    }

    @Test
    void getConversation_success() {
        UUID currentUserId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        Account current = new Account();
        current.setPublicId(currentUserId);

        Message message = new Message();
        Page<Message> page = new PageImpl<>(List.of(message));

        when(accountService.getAuthenticatedAccount()).thenReturn(current);
        when(messageRepo.findConversation(eq(currentUserId), eq(otherUserId), any()))
                .thenReturn(page);
        when(messageMapper.toMessageDto(message)).thenReturn(new MessageDto());

        Page<MessageDto> result = messageService.getConversation(otherUserId, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(messageRepo).findConversation(currentUserId, otherUserId, Pageable.unpaged());
    }

    @Test
    void markAsRead_success() {
        UUID senderPublicId = UUID.randomUUID();
        UUID receiverPublicId = UUID.randomUUID();
        Instant limit = Instant.now();

        Account sender = new Account();
        sender.setPublicId(senderPublicId);

        when(accountService.getAuthenticatedAccount()).thenReturn(sender);
        when(messageRepo.markAsReadUpTo(senderPublicId, receiverPublicId, limit))
                .thenReturn(3);

        int result = messageService.markAsRead(limit, receiverPublicId);

        assertEquals(3, result);
        verify(messageRepo).markAsReadUpTo(senderPublicId, receiverPublicId, limit);
    }

    @Test
    void wasConversation_true_whenBothDirectionsExist() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        Account a1 = new Account();
        a1.setPublicId(user1);

        Account a2 = new Account();
        a2.setPublicId(user2);

        Message m1 = new Message();
        m1.setSender(a1);
        m1.setReceiver(a2);

        Message m2 = new Message();
        m2.setSender(a2);
        m2.setReceiver(a1);

        Page<Message> page = new PageImpl<>(List.of(m1, m2));

        when(messageRepo.findConversation(eq(user1), eq(user2), eq(Pageable.unpaged())))
                .thenReturn(page);

        assertTrue(messageService.wasConversation(user1, user2));
    }

    @Test
    void wasConversation_false_whenOneDirectionMissing() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        Account a1 = new Account();
        a1.setPublicId(user1);

        Account a2 = new Account();
        a2.setPublicId(user2);

        Message m1 = new Message();
        m1.setSender(a1);
        m1.setReceiver(a2);

        Page<Message> page = new PageImpl<>(List.of(m1));

        when(messageRepo.findConversation(eq(user1), eq(user2), eq(Pageable.unpaged())))
                .thenReturn(page);

        assertFalse(messageService.wasConversation(user1, user2));
    }
}
