package com.frinkan.dto;

import java.util.List;

public class ProfileDto {
    private String nick;
    private boolean active;
    private String firstName;
    private String lastName;
    private int respondTime;
    private int messagePrice;
    private int callPrice;
    private String profilePicturePath;
    private List<String> linkedAccounts; // Konta social media

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean getActive() {
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
}
