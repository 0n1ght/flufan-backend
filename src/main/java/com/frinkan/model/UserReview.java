package com.frinkan.model;

import jakarta.persistence.Embeddable;

@Embeddable  // Klasa embeddable, aby była używana w ElementCollection
public class UserReview {

    private String username;
    private String opinion;

    public UserReview(String username, String opinion) {
        this.username = username;
        this.opinion = opinion;
    }

    public UserReview() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }
}
