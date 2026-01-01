package com.flufan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flufan.dto.ProductDto;
import com.flufan.dto.StripeResponse;

public interface StripeService {
    StripeResponse checkoutProducts(ProductDto productDto) throws JsonProcessingException;
}
