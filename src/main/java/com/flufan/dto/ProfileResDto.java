package com.flufan.dto;

import com.flufan.model.LinkedAccount;
import com.flufan.entity.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResDto {
    private Long id;
    private String nick;
    private String title;
    private boolean verified;
    private boolean active;
    private String firstName;
    private String lastName;
    private int interactionCounter;
    private double rating;
    private int respondTime;
    private long messagePrice;
    private long callPrice;
    private List<LinkedAccount> linkedAccounts;
    private List<Service> menu;
    private Long accountId;
}
