package com.flufan.modules.user.entity;

import com.flufan.common.model.Notification;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

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

    @Column(unique = true, updatable = false, nullable = false)
    private UUID publicId = UUID.randomUUID();

    @Column(unique = true, nullable = false)
    private String username;
    private LocalDateTime lastUsernameChange = null;

    @Column(unique = true, nullable = false)
    private String email;
    private boolean verifiedEmail = false;
    private String password;

    @ElementCollection
    private List<Notification> notifications = new ArrayList<>();

    @ElementCollection
    @MapKeyColumn(name = "receiver_id")
    @Column(name = "message_count")
    private Map<UUID, Long> availableReplies = new HashMap<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    private LocalDateTime deletedAt;

    public Account(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
