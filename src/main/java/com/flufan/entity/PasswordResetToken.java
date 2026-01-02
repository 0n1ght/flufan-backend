package com.flufan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private Instant expiryDate = Instant.now().plusSeconds(3600);

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
