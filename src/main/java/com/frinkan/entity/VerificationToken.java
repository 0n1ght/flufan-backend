package com.frinkan.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    public VerificationToken() {}

    public VerificationToken(String token, String email) {
        this.token = token;
        this.email = email;
        this.expiresAt = LocalDateTime.now().plusMinutes(20);
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
