package com.flufan.modules.user.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String username;
    private String password;
}
