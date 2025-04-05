package com.frinkan.service.impl;

import com.frinkan.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.frinkan.dto.ProductRequest;
import com.frinkan.dto.StripeResponse;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.BLIK)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.P24)
                // Karta kredytowa – dzięki niej na urządzeniach Apple/Android wyświetlą się Apple Pay/Google Pay
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD);

        try {
            paramsBuilder.addPaymentMethodType(SessionCreateParams.PaymentMethodType.valueOf("PAYPAL"));
        } catch (IllegalArgumentException _) {}

        paramsBuilder.addLineItem(lineItem);

        if (productRequest.getEmail() != null) {
            paramsBuilder.setCustomerEmail(productRequest.getEmail());
        }

        SessionCreateParams params = paramsBuilder.build();

        try {
            Session session = Session.create(params);
            return new StripeResponse("SUCCESS",
                    "Payment session created",
                    session.getId(),
                    session.getUrl());
        } catch (StripeException e) {
            return new StripeResponse("FAILED",
                    "StripeException: " + e.getMessage(),
                    null,
                    null);
        }
    }
}
