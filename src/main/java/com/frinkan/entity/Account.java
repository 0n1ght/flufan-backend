package com.frinkan.entity;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;
    private int notifications = 0;

    @ElementCollection
    @MapKeyColumn(name = "receiver_id")
    @Column(name = "message_count")
    private Map<Long, Long> availableMessages = new HashMap<>();

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<Long, Long> getAvailableMessages() {
        return availableMessages;
    }

    public void setAvailableMessages(Map<Long, Long> availableMessages) {
        this.availableMessages = availableMessages;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
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
}
