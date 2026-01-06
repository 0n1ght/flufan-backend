package com.flufan.modules.user.dto;

import jakarta.persistence.ElementCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ServiceDto {
    private String title;
    private String description;
    private long price;

    @ElementCollection
    private List<String> optionalQuestions = new ArrayList<>();
}
