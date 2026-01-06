package com.flufan.common.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkedAccount {
    private String platform;
    private String identifier;
}
