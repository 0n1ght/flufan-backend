package com.flufan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flufan.dto.ProductDto;
import com.flufan.dto.StripeResponse;
import com.flufan.service.StripeService;
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
    private final StripeService stripeService;

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductDto productDto) throws JsonProcessingException {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productDto);
        HttpStatus status = stripeResponse.getStatus().equals("SUCCESS") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(stripeResponse);
    }
}
