package com.flufan.modules.user.dto;

import com.flufan.common.model.LinkedAccount;
import lombok.Data;

import java.util.List;

@Data
public class ProfileDto {
    private String title;
    private boolean active;
    private String nick;
    private String firstName;
    private String lastName;
    private int respondTime;
    private int messagePrice;
    private int callPrice;
    private List<LinkedAccount> linkedAccounts;
    private List<ServiceDto> menu;
}
