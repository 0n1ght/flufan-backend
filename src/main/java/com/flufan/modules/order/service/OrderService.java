package com.flufan.modules.order.service;

import java.util.UUID;

public interface OrderService {
    void realiseMessage(UUID buyerPublicId, UUID sellerPublicId, String content, String details);
    void realiseCall(UUID buyerPublicId, UUID sellerPublicId);
    void realiseService(UUID buyerPublicId, UUID sellerPublicId, String productName, long quantity, String details);
}
