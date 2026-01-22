package com.flufan.modules.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flufan.modules.order.dto.ProductDto;
import com.flufan.modules.order.dto.StripeResponse;
import com.flufan.modules.order.client.StripeClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/v1")
@RequiredArgsConstructor
public class ProductCheckoutController {
    private final StripeClient stripeClient;

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductDto productDto) throws JsonProcessingException {
        StripeResponse stripeResponse = stripeClient.checkoutProducts(productDto);
        HttpStatus status = stripeResponse.getStatus().equals("SUCCESS") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(stripeResponse);
    }
}
