package com.frinkan.dto;

public class ProductRequest {
    private Long amount;
    private Long quantity;
    private String name;
    private String productType;
    private Long sellerId;
    private String currency;
    private String email;

    public ProductRequest() {
    }

    public ProductRequest(Long amount, Long quantity, String name, String productType, Long sellerId, String currency, String email) {
        this.amount = amount;
        this.quantity = quantity;
        this.name = name;
        this.productType = productType;
        this.sellerId = sellerId;
        this.currency = currency;
        this.email = email;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
