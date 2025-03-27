package com.frinkan.entity;

import com.frinkan.model.LinkedAccount;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nick;
    private String title;
    private boolean verified;
    private boolean active = true;
    private String firstName;
    private String lastName;

    private int interactionCounter;
    private double rating;
    private int respondTime;

    private int messagePrice;
    private int callPrice;
    private String profilePicturePath;

    @ElementCollection
    @CollectionTable(name = "linked_accounts", joinColumns = @JoinColumn(name = "profile_id"))
    private List<LinkedAccount> linkedAccounts;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private List<Service> menu;

    @OneToOne(mappedBy = "profile")
    private Account account;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getInteractionCounter() {
        return interactionCounter;
    }

    public void setInteractionCounter(int interactionCounter) {
        this.interactionCounter = interactionCounter;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRespondTime() {
        return respondTime;
    }

    public void setRespondTime(int respondTime) {
        this.respondTime = respondTime;
    }

    public int getMessagePrice() {
        return messagePrice;
    }

    public void setMessagePrice(int messagePrice) {
        this.messagePrice = messagePrice;
    }

    public int getCallPrice() {
        return callPrice;
    }

    public void setCallPrice(int callPrice) {
        this.callPrice = callPrice;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public List<LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<LinkedAccount> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public List<Service> getMenu() {
        return menu;
    }

    public void setMenu(List<Service> menu) {
        this.menu = menu;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
