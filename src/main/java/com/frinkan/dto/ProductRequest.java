package com.frinkan.dto;

import com.frinkan.enums.ProductType;

public class ProductRequest {
    private Long quantity;
    private ProductType productType;
    private String name;
    private String details;
    private Long sellerId;
    private String currency;

    public ProductRequest() {
    }

    public ProductRequest(Long quantity, String name, ProductType productType, String details, Long sellerId, String currency) {
        this.quantity = quantity;
        this.name = name;
        this.productType = productType;
        this.details = details;
        this.sellerId = sellerId;
        this.currency = currency;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
