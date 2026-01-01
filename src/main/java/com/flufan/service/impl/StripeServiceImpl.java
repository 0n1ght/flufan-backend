package com.flufan.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flufan.entity.Account;
import com.flufan.enums.ProductType;
import com.flufan.service.AccountService;
import com.flufan.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.flufan.dto.ProductDto;
import com.flufan.dto.StripeResponse;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    public StripeResponse checkoutProducts(ProductDto productDto) throws JsonProcessingException {
        Account authenticatedAccount = accountService.getAuthenticatedAccount();
        Account sellerAccount = accountService.findByPublicId(productDto.sellerPublicId());

        if (productDto.quantity() == null || productDto.quantity() <= 0 ||
                productDto.name() == null || productDto.name().isEmpty() ||
                (productDto.productType() == ProductType.MESSAGE &&
                        (sellerAccount.getProfile().getMessagePrice() == -1 || productDto.quantity() != 1)) ||
                (productDto.productType() == ProductType.CALL &&
                        (sellerAccount.getProfile().getCallPrice() == -1 || productDto.quantity() != 1)) ||
                (productDto.productType() == ProductType.SERVICE && sellerAccount.getProfile().getMenu().stream()
                        .noneMatch(service -> productDto.name().equalsIgnoreCase(service.getTitle()))) ||
                (productDto.productType() == ProductType.MESSAGE && productDto.name().length() > 700) ||
                !sellerAccount.getProfile().isActive()) {
            return new StripeResponse("FAILED", "Invalid request parameters", null, null);
        }

        Stripe.apiKey = secretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productDto.name())
                        .build();

        long priceAmount = getPriceAmount(productDto, sellerAccount);

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productDto.currency() != null ? productDto.currency() : "PLN")
                        .setUnitAmount(priceAmount)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productDto.quantity())
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
                .putMetadata("product_type", productDto.productType() != null ? productDto.productType().name() : "unknown")
                .putMetadata("product_name", productDto.name())
                .putMetadata("product_details", productDto.details() != null ? productDto.details() : "unknown")
                .putMetadata("buyer_id", authenticatedAccount.getId() != null ? String.valueOf(authenticatedAccount.getId()) : "-1")
                .putMetadata("seller_id", productDto.sellerPublicId() != null ? String.valueOf(productDto.sellerPublicId()) : "-1")
                .putMetadata("quantity", String.valueOf(productDto.quantity()))
                .putMetadata("email", authenticatedAccount.getEmail() != null ? authenticatedAccount.getEmail() : "not_provided")
                .setCustomerEmail(authenticatedAccount.getEmail());

        if (productDto.productType() == ProductType.SERVICE) {
            Map<String, String> purchaseQa = getStringStringMap(productDto, sellerAccount);

            paramsBuilder.putMetadata("purchase_qa", objectMapper.writeValueAsString(purchaseQa));
        }

        try {
            Session session = Session.create(paramsBuilder.build());
            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException e) {
            return new StripeResponse("FAILED", "Stripe error: " + e.getMessage(), null, null);
        }
    }

    private static Map<String, String> getStringStringMap(ProductDto productDto, Account sellerAccount) {
        List<String> questions = new ArrayList<>();
        for (com.flufan.entity.Service service : sellerAccount.getProfile().getMenu()) {
            if (productDto.name().equalsIgnoreCase(service.getTitle())) {
                questions = service.getOptionalQuestions();
                break;
            }
        }
        Map<String, String> purchaseQa = new HashMap<>();
        List<String> answers = productDto.optionalAnswers();
        if (answers != null && questions.size() == answers.size()) {
            for (int i = 0; i < questions.size(); i++) {
                if (!"".equals(answers.get(i))) {
                    purchaseQa.put(questions.get(i), answers.get(i));
                }
            }
        }
        return purchaseQa;
    }

    private static long getPriceAmount(ProductDto productDto, Account sellerAccount) {
        long priceAmount = 0;
        switch (productDto.productType()) {
            case MESSAGE -> priceAmount = sellerAccount.getProfile().getMessagePrice();
            case CALL -> priceAmount = sellerAccount.getProfile().getCallPrice();
            case SERVICE -> {
                for (com.flufan.entity.Service service : sellerAccount.getProfile().getMenu()) {
                    if (productDto.name().equalsIgnoreCase(service.getTitle())) {
                        priceAmount = service.getPrice();
                    }
                }
            }
            default -> throw new IllegalArgumentException("Incorrect Product Type: " + productDto.productType());
        }
        return priceAmount;
    }
}
