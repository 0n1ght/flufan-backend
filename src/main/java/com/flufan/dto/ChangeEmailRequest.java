package com.flufan.dto;

import lombok.Data;

@Data
public class ChangeEmailRequest {
    private String password;
    private String newEmail;
}
