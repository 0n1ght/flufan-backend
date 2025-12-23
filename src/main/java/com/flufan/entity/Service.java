package com.flufan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private long price;

    @ElementCollection
    private List<String> optionalQuestions = new ArrayList<>();

    public Service(String title, String description, long price) {
        this.title = title;
        this.description = description;
        this.price = price;
    }
}
