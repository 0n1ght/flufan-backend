package com.frinkan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile; // Profil, który otrzymuje recenzję

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Account reviewer; // Użytkownik, który wystawia opinię

    private int rating; // Ocena (np. 1-5 gwiazdek)

    @Column(columnDefinition = "TEXT")
    private String comment; // Komentarz do opinii

    private LocalDateTime createdAt;

    public UserReview() {
        this.createdAt = LocalDateTime.now();
    }

    public UserReview(Profile profile, Account reviewer, int rating, String comment) {
        this.profile = profile;
        this.reviewer = reviewer;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Account getReviewer() {
        return reviewer;
    }

    public void setReviewer(Account reviewer) {
        this.reviewer = reviewer;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
