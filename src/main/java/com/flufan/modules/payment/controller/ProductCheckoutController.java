package com.flufan.modules.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flufan.modules.payment.dto.ProductDto;
import com.flufan.modules.payment.dto.StripeResponse;
import com.flufan.modules.payment.service.StripeService;
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
