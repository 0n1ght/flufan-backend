package com.flufan.modules.user.entity;

import com.flufan.common.model.LinkedAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private UUID publicId = UUID.randomUUID();

    @Column(nullable = false)
    private String nick;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean active = true;
    private String firstName;
    private String lastName;
    private int interactionCounter;
    private double rating;
    private int respondTime;
    private long messagePrice;
    private long callPrice;

    @ElementCollection
    @CollectionTable(name = "linked_accounts", joinColumns = @JoinColumn(name = "profile_id"))
    private List<LinkedAccount> linkedAccounts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private List<Service> menu = new ArrayList<>();

    @OneToOne(mappedBy = "profile")
    private Account account;

    @PreRemove
    private void preRemove() {
        if (account != null) {
            account.setProfile(null);
        }
    }
}
