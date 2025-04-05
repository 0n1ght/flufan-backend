package com.frinkan.service;

import com.frinkan.dto.ProductRequest;
import com.frinkan.dto.StripeResponse;

public interface StripeService {
    StripeResponse checkoutProducts(ProductRequest productRequest);
}
