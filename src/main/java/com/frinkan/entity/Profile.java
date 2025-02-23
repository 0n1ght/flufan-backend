package com.frinkan.entity;

import com.frinkan.model.UserReview;
import com.frinkan.model.Service;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nick;
    private String firstName;
    private String lastName;
    private int interactionCounter;
    private String respondTime;
    private int messagePrice;
    private int callPrice;
    private String profilePicturePath;  // Ścieżka do zdjęcia profilowego

    @ElementCollection
    @CollectionTable(name = "user_reviews", joinColumns = @JoinColumn(name = "profile_id"))
    private List<UserReview> reviews; // Lista opinii

    @OneToMany
    @JoinColumn(name = "profile_id")
    private List<Service> menu; // Lista usług

    @OneToOne(mappedBy = "profile") // Powiązanie z klasą Account
    private Account account;

    public Profile(String nick, String firstName, String lastName, int interactionCounter, String respondTime,
                   int messagePrice, int callPrice, String profilePicturePath, List<UserReview> reviews, List<Service> menu) {
        this.nick = nick;
        this.firstName = firstName;
        this.lastName = lastName;
        this.interactionCounter = interactionCounter;
        this.respondTime = respondTime;
        this.messagePrice = messagePrice;
        this.callPrice = callPrice;
        this.profilePicturePath = profilePicturePath;
        this.reviews = reviews;
        this.menu = menu;
    }

    public Profile() {
    }

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

    public String getRespondTime() {
        return respondTime;
    }

    public void setRespondTime(String respondTime) {
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

    public List<UserReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<UserReview> reviews) {
        this.reviews = reviews;
    }

    public List<Service> getMenu() {
        return menu;
    }

    public void setMenu(List<Service> menu) {
        this.menu = menu;
    }
}
