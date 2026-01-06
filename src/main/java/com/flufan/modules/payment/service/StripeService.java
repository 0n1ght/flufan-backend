package com.flufan.modules.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flufan.modules.payment.dto.ProductDto;
import com.flufan.modules.payment.dto.StripeResponse;

public interface StripeService {
    StripeResponse checkoutProducts(ProductDto productDto) throws JsonProcessingException;
}
