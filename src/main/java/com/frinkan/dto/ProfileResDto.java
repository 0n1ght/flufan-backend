package com.frinkan.dto;

import com.frinkan.entity.Account;
import com.frinkan.model.Service;
import com.frinkan.model.UserReview;

import java.util.List;

public class ProfileResDto {
    private Long id;

    private String nick;
    private boolean verified;
    private boolean active;
    private String firstName;
    private String lastName;

    private int interactionCounter;
    private double rating;
    private int respondTime;

    private int messagePrice;
    private int callPrice;
    private String profilePicturePath;
    private List<String> linkedAccounts;
    private List<UserReview> reviews;
    private List<Service> menu;
    private Long accountId;

    public ProfileResDto(Long id, String nick, boolean verified, boolean active, String firstName,
                         String lastName, int interactionCounter, double rating, int respondTime, int messagePrice,
                         int callPrice, String profilePicturePath, List<String> linkedAccounts, List<UserReview> reviews,
                         List<Service> menu, Long accountId) {
        this.id = id;
        this.nick = nick;
        this.verified = verified;
        this.active = active;
        this.firstName = firstName;
        this.lastName = lastName;
        this.interactionCounter = interactionCounter;
        this.rating = rating;
        this.respondTime = respondTime;
        this.messagePrice = messagePrice;
        this.callPrice = callPrice;
        this.profilePicturePath = profilePicturePath;
        this.linkedAccounts = linkedAccounts;
        this.reviews = reviews;
        this.menu = menu;
        this.accountId = accountId;
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

    public List<String> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void setLinkedAccounts(List<String> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
