package com.flufan.modules.payment.dto;

import com.flufan.modules.payment.enums.ProductType;

import java.util.List;
import java.util.UUID;

public record ProductDto(
        Long quantity,
        ProductType productType,
        String name,
        String details,
        UUID sellerPublicId,
        String currency,
        List<String> optionalAnswers
) {}
