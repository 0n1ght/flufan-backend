package com.flufan.modules.user.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AccountDto {
    private UUID publicId;
    private String username;
    private ProfileDto profile;
}
