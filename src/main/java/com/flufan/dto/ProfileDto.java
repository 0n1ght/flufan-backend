package com.flufan.dto;

import com.flufan.model.LinkedAccount;
import com.flufan.entity.Service;
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
    private String profilePicturePath;
    private List<LinkedAccount> linkedAccounts;
    private List<Service> menu;
}
