package com.flufan.entity;

import com.flufan.model.Notification;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
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
}
