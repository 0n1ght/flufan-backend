package com.flufan.dto;

public class AccountDto {
    private Long id;
    private String username;
    private String email;
    private ProfileDto profile;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProfileDto getProfile() {
        return profile;
    }

    public void setProfile(ProfileDto profile) {
        this.profile = profile;
    }
}
