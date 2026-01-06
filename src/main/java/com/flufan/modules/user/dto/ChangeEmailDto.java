package com.flufan.modules.user.dto;

public record ChangeEmailDto(
        String password,
        String newEmail
) {}
