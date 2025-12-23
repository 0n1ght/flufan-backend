package com.flufan.entity;

import com.flufan.model.LinkedAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String nick;
    private String title;
    private boolean verified;
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
}
