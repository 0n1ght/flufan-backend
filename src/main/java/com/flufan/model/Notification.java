package com.flufan.model;

import com.flufan.enums.NotificationType;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private NotificationType type;
    private String content;
}
