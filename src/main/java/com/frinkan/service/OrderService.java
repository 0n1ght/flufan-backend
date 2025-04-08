package com.frinkan.service;

public interface OrderService {
    void addMessageToAcc(long buyerId, long sellerId, long quantity);
    void addCallToAcc(long buyerId, long sellerId);
    void addServiceToAcc(long buyerId, long sellerId, long quantity);
}
