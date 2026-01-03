package com.flufan.model;

import com.flufan.enums.NotificationType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.Instant;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String content;
    private Instant instant = Instant.now();
}
