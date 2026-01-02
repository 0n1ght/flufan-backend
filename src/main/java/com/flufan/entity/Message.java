package com.flufan.entity;

import com.flufan.enums.MessageType;
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
