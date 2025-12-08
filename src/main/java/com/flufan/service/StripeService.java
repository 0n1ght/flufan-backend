package com.flufan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flufan.dto.ProductRequest;
import com.flufan.dto.StripeResponse;

public interface StripeService {
    StripeResponse checkoutProducts(ProductRequest productRequest) throws JsonProcessingException;
}
