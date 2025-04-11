package com.frinkan.service;

public interface OrderService {
    void realiseMessage(long buyerId, long sellerId, String content, String details);
    void realiseCall(long buyerId, long sellerId);
    void realiseService(long buyerId, long sellerId, String productName, long quantity, String details);
}
