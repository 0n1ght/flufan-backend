package com.frinkan.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frinkan.entity.Account;
import com.frinkan.enums.ProductType;
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

import java.util.*;

@Service
public class StripeServiceImpl implements StripeService {
    private AccountService accountService;
    private ObjectMapper objectMapper;

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    public StripeServiceImpl(AccountService accountService, ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    public StripeResponse checkoutProducts(ProductRequest productRequest) throws JsonProcessingException {
        Account authenticatedAccount = accountService.getAuthenticatedAccount();
        Account sellerAccount = accountService.getById(productRequest.getSellerId());

        if (productRequest.getQuantity() == null || productRequest.getQuantity() <= 0 ||
                productRequest.getName() == null || productRequest.getName().isEmpty() ||
                (productRequest.getProductType() == ProductType.MESSAGE &&
                        (sellerAccount.getProfile().getMessagePrice() == -1 || productRequest.getQuantity() != 1)) ||
                (productRequest.getProductType() == ProductType.CALL &&
                        (sellerAccount.getProfile().getCallPrice() == -1 || productRequest.getQuantity() != 1)) ||
                (productRequest.getProductType() == ProductType.SERVICE && sellerAccount.getProfile().getMenu().stream()
                        .noneMatch(service -> productRequest.getName().equalsIgnoreCase(service.getTitle()))) ||
                (productRequest.getProductType() == ProductType.MESSAGE && productRequest.getName().length() > 700) ||
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
                .putMetadata("product_details", productRequest.getDetails() != null ? productRequest.getDetails() : "unknown")
                .putMetadata("buyer_id", authenticatedAccount.getId() != null ? String.valueOf(authenticatedAccount.getId()) : "-1")
                .putMetadata("seller_id", productRequest.getSellerId() != null ? String.valueOf(productRequest.getSellerId()) : "-1")
                .putMetadata("quantity", String.valueOf(productRequest.getQuantity()))
                .putMetadata("email", authenticatedAccount.getEmail() != null ? authenticatedAccount.getEmail() : "not_provided")
                .setCustomerEmail(authenticatedAccount.getEmail());

        if (productRequest.getProductType() == ProductType.SERVICE) {
            Map<String, String> purchaseQa = getStringStringMap(productRequest, sellerAccount);

            paramsBuilder.putMetadata("purchase_qa", objectMapper.writeValueAsString(purchaseQa));
        }

        try {
            Session session = Session.create(paramsBuilder.build());
            return new StripeResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
        } catch (StripeException e) {
            return new StripeResponse("FAILED", "Stripe error: " + e.getMessage(), null, null);
        }
    }

    private static Map<String, String> getStringStringMap(ProductRequest productRequest, Account sellerAccount) {
        List<String> questions = new ArrayList<>();
        for (com.frinkan.entity.Service service : sellerAccount.getProfile().getMenu()) {
            if (productRequest.getName().equalsIgnoreCase(service.getTitle())) {
                questions = service.getOptionalQuestions();
                break;
            }
        }
        Map<String, String> purchaseQa = new HashMap<>();
        List<String> answers = productRequest.getOptionalAnswers();
        if (answers != null && questions.size() == answers.size()) {
            for (int i = 0; i < questions.size(); i++) {
                if (!"".equals(answers.get(i))) {
                    purchaseQa.put(questions.get(i), answers.get(i));
                }
            }
        }
        return purchaseQa;
    }

    private static long getPriceAmount(ProductRequest productRequest, Account sellerAccount) {
        long priceAmount = 0;
        switch (productRequest.getProductType()) {
            case MESSAGE -> priceAmount = sellerAccount.getProfile().getMessagePrice();
            case CALL -> priceAmount = sellerAccount.getProfile().getCallPrice();
            case SERVICE -> {
                for (com.frinkan.entity.Service service : sellerAccount.getProfile().getMenu()) {
                    if (productRequest.getName().equalsIgnoreCase(service.getTitle())) {
                        priceAmount = service.getPrice();
                    }
                }
            }
            default -> throw new IllegalArgumentException("Incorrect Product Type: " + productRequest.getProductType());
        }
        return priceAmount;
    }
}
