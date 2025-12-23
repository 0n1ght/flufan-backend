package com.flufan.dto;

import com.flufan.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private Long quantity;
    private ProductType productType;
    private String name;
    private String details;
    private Long sellerId;
    private String currency;
    private List<String> optionalAnswers;
}
