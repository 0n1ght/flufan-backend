package com.flufan.modules.chat.entity;

import com.flufan.modules.chat.enums.MessageType;
import com.flufan.modules.user.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiver;

    @Column(nullable = false)
    private String content;
    private boolean readStatus = false;

    @Column(nullable = false)
    private Instant sentAt;

    @Column(nullable = false)
    private MessageType messageType;
}
