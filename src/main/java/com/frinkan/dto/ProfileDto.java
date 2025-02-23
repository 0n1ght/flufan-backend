package com.frinkan.dto;

import com.frinkan.model.Service;

import java.util.List;

public class ProfileDto {
    private String nick;
    private String firstName;
    private String lastName;
    private String respondTime;
    private int messagePrice;
    private int callPrice;
    private String profilePicturePath;
    private List<Service> menu;
}
