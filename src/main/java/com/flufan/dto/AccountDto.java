package com.flufan.dto;

import lombok.Data;

@Data
public class AccountDto {
    private Long id;
    private String username;
    private ProfileDto profile;
}
