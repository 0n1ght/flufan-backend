package com.flufan.dto;

import com.flufan.model.LinkedAccount;
import com.flufan.entity.Service;

import java.util.List;

public class ProfileResDto {
    private Long id;

    private String nick;
    private String title;
    private boolean verified;
    private boolean active;
    private String firstName;
    private String lastName;

    private int interactionCounter;
    private double rating;
    private int respondTime;

    private long messagePrice;
    private long callPrice;
    private List<LinkedAccount> linkedAccounts;
    private List<Service> menu;
    private Long accountId;

    public ProfileResDto(Long id, String nick, String title, boolean verified, boolean active, String firstName,
                         String lastName, int interactionCounter, double rating, int respondTime, long messagePrice,
                         long callPrice, List<LinkedAccount> linkedAccounts,
                         List<Service> menu, Long accountId) {
        this.id = id;
        this.nick = nick;
        this.title = title;
        this.verified = verified;
        this.active = active;
        this.firstName = firstName;
        this.lastName = lastName;
        this.interactionCounter = interactionCounter;
        this.rating = rating;
        this.respondTime = respondTime;
        this.messagePrice = messagePrice;
        this.callPrice = callPrice;
        this.linkedAccounts = linkedAccounts;
        this.menu = menu;
        this.accountId = accountId;
    }

    public ProfileResDto() {

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

    public long getMessagePrice() {
        return messagePrice;
    }

    public void setMessagePrice(int messagePrice) {
        this.messagePrice = messagePrice;
    }

    public long getCallPrice() {
        return callPrice;
    }

    public void setCallPrice(int callPrice) {
        this.callPrice = callPrice;
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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
