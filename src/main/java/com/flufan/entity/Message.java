package com.flufan.entity;

import com.flufan.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    private String content;
    private boolean readStatus = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt = new Date();

    private MessageType messageType;
}
