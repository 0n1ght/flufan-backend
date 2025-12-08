package com.flufan.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
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

    public Service() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<String> getOptionalQuestions() {
        return optionalQuestions;
    }

    public void setOptionalQuestions(List<String> optionalQuestions) {
        this.optionalQuestions = optionalQuestions;
    }
}
