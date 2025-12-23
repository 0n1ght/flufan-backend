package com.flufan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewDto {
    private Long id;
    private Long profileId;
    private Long reviewerId;
    private int rating;
    private String comment;
}
