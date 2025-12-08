package com.flufan.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class LinkedAccount {
    private String platform;
    private String identifier;

    public LinkedAccount() {}

    public LinkedAccount(String platform, String identifier) {
        this.platform = platform;
        this.identifier = identifier;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
