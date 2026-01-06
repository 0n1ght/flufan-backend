package com.flufan.modules.user.dto;

public record ChangePasswordDto(
        String oldPassword,
        String newPassword
) {}
