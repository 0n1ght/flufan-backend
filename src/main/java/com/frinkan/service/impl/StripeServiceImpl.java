package com.frinkan.service.impl;

import com.frinkan.entity.Account;
import com.frinkan.enums.ProductType;
import com.frinkan.exception.ServiceNotFoundException;
import com.frinkan.service.AccountService;
import com.frinkan.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.frinkan.dto.ProductRequest;
import com.frinkan.dto.StripeResponse;

import java.util.List;
import java.util.Objects;

@Service
public class StripeServiceImpl implements StripeService {

    @Autowired
    private AccountService accountService;

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        Account authenticatedAccount = accountService.getAuthenticatedAccount();
        Account sellerAccount = accountService.getById(productRequest.getSellerId());

        if (productRequest.getQuantity() == null || productRequest.getQuantity() <= 0 ||
                productRequest.getName() == null || productRequest.getName().isEmpty() ||
                (productRequest.getProductType().equals(ProductType.MESSAGE) && sellerAccount.getProfile().getMessagePrice() == -1) ||
                (productRequest.getProductType().equals(ProductType.CALL) && sellerAccount.getProfile().getCallPrice() == -1) ||
                (productRequest.getProductType().equals(ProductType.SERVICE) && sellerAccount.getProfile().getMenu().stream()
                        .noneMatch(service -> productRequest.getName().equalsIgnoreCase(service.getTitle()))) ||
                !sellerAccount.getProfile().isActive()) {
            return new StripeResponse("FAILED", "Invalid request parameters", null, null);
        }

        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        long priceAmount = getPriceAmount(productRequest, sellerAccount);

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "PLN")
                        .setUnitAmount(priceAmount)
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
                .putMetadata("product_type", productRequest.getProductType() != null ? productRequest.getProductType().name() : "unknown")
                .putMetadata("product_name", productRequest.getName() != null ? productRequest.getName() : "unknown")
                .putMetadata("buyer_id", authenticatedAccount.getId() != null ? String.valueOf(authenticatedAccount.getId()) : "-1")
                .putMetadata("seller_id", productRequest.getSellerId() != null ? String.valueOf(productRequest.getSellerId()) : "-1")
                .putMetadata("quantity", String.valueOf(productRequest.getQuantity()))
                .putMetadata("email", authenticatedAccount.getEmail() != null ? authenticatedAccount.getEmail() : "not_provided")
                .setCustomerEmail(authenticatedAccount.getEmail());

        try {
            Session session = Session.create(paramsBuilder.build());
            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException e) {
            return new StripeResponse("FAILED", "Stripe error: " + e.getMessage(), null, null);
        }
    }

    private static long getPriceAmount(ProductRequest productRequest, Account sellerAccount) {
        long priceAmount = 0;
        switch (productRequest.getProductType()) {
            case MESSAGE -> priceAmount = sellerAccount.getProfile().getMessagePrice();
            case CALL -> priceAmount = sellerAccount.getProfile().getCallPrice();
            case SERVICE -> {
                for (com.frinkan.entity.Service service : sellerAccount.getProfile().getMenu()) {
                    if (Objects.equals(service.getTitle(), productRequest.getName())) {
                        priceAmount = service.getPrice();
                    }
                }
            }
            default -> throw new IllegalArgumentException("Incorrect Product Type: " + productRequest.getProductType());
        }
        return priceAmount;
    }
}
