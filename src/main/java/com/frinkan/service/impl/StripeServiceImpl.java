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

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        if (productRequest.getAmount() == null || productRequest.getAmount() <= 0 ||
                productRequest.getQuantity() == null || productRequest.getQuantity() <= 0 ||
                productRequest.getName() == null || productRequest.getName().isEmpty()) {
            return new StripeResponse("FAILED", "Invalid request parameters", null, null);
        }

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
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.BLIK)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.P24)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.PAYPAL)
                .addLineItem(lineItem)
                .putMetadata("product_type", productRequest.getProductType() != null ? productRequest.getProductType() : "unknown")
                .putMetadata("product_name", productRequest.getName() != null ? productRequest.getName() : "unknown")
                .putMetadata("buyer_id", productRequest.getBuyerId() != null ? String.valueOf(productRequest.getBuyerId()) : "-1")
                .putMetadata("seller_id", productRequest.getSellerId() != null ? String.valueOf(productRequest.getSellerId()) : "-1")
                .putMetadata("quantity", String.valueOf(productRequest.getQuantity()))
                .putMetadata("email", productRequest.getEmail() != null ? productRequest.getEmail() : "not_provided");

        if (productRequest.getEmail() != null) {
            paramsBuilder.setCustomerEmail(productRequest.getEmail());
        }

        try {
            Session session = Session.create(paramsBuilder.build());
            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException e) {
            return new StripeResponse("FAILED", "Stripe error: " + e.getMessage(), null, null);
        }
    }
}
