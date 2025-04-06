package com.frinkan.controller;

import com.frinkan.service.AccountService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private AccountService accountService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) throws IOException {
        String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                String email = session.getCustomerEmail();
                String sessionId = session.getId();
                String productType = session.getMetadata().get("product_type");
                String sellerId = session.getMetadata().get("seller_id");

                System.out.println("Payment completed:");
                System.out.println("Email: " + email);
                System.out.println("Session ID: " + sessionId);
                System.out.println("Product Type: " + productType);
                System.out.println("Seller ID: " + sellerId);
                //todo
                // iteracja po opcjach, dodawanie mozliwosci do konta
            }
        }

        return ResponseEntity.ok("Received");
    }
}
