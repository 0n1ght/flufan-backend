package com.frinkan.dto;

public class UserReviewDto {
    private Long id;
    private Long profileId;
    private Long reviewerId;
    private int rating;
    private String comment;

    public UserReviewDto() {}

    public UserReviewDto(Long id, Long profileId, Long reviewerId, int rating, String comment) {
        this.id = id;
        this.profileId = profileId;
        this.reviewerId = reviewerId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
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
}
