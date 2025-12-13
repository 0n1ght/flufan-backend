package com.flufan.entity;

import com.flufan.model.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String username;
    private LocalDateTime lastUsernameChange = null;
    private String email;
    private boolean verifiedEmail = false;
    private String password;

    @ElementCollection
    private List<Notification> notifications = new ArrayList<>();

    @ElementCollection
    @MapKeyColumn(name = "receiver_id")
    @Column(name = "message_count")
    private Map<Long, Long> availableReplies = new HashMap<>();

    @OneToOne
    private Profile profile;

    public Account(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerifiedEmail() {
        return verifiedEmail;
    }

    public void setVerifiedEmail(boolean verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<Long, Long> getAvailableReplies() {
        return availableReplies;
    }

    public void setAvailableReplies(Map<Long, Long> availableReplies) {
        this.availableReplies = availableReplies;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public LocalDateTime getLastUsernameChange() {
        return lastUsernameChange;
    }

    public void setLastUsernameChange(LocalDateTime lastUsernameChange) {
        this.lastUsernameChange = lastUsernameChange;
    }
}
