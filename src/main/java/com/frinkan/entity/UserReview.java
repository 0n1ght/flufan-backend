package com.frinkan.entity;

import jakarta.persistence.*;

@Entity
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String opinion;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public UserReview() {
    }

    public UserReview(String username, String opinion, Profile profile) {
        this.username = username;
        this.opinion = opinion;
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
