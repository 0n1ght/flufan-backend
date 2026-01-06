package com.flufan.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewDto {
    private Long id;
    private UUID profilePublicId;
    private UUID reviewerPublicId;
    private int rating;
    private String comment;
}
