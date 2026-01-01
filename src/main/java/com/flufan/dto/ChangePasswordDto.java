package com.flufan.dto;

public record ChangePasswordDto(
        String oldPassword,
        String newPassword
) {}
