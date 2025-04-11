package com.frinkan.model;

import com.frinkan.enums.NotificationType;
import jakarta.persistence.Embeddable;

@Embeddable
public class Notification {

    private NotificationType type;
    private String content;

    public Notification(NotificationType type, String content) {
        this.type = type;
        this.content = content;
    }

    public Notification() {
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
