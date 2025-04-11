package com.frinkan.service;

public interface OrderService {
    void realiseMessage(long buyerId, long sellerId, long quantity);
    void realiseCall(long buyerId, long sellerId);
    void realiseService(long buyerId, long sellerId, String productName, long quantity);
}
