package com.frinkan.controller;

import com.frinkan.service.OrderService;
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
    private OrderService orderService;

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
                String productType = session.getMetadata().get("product_type");
                String productName = session.getMetadata().get("product_name");
                long buyerId = Long.parseLong(session.getMetadata().get("buyer_id"));
                long sellerId = Long.parseLong(session.getMetadata().get("seller_id"));
                long quantity = Long.parseLong(session.getMetadata().get("quantity"));

                switch (productType.toLowerCase()) {
                    case "message" -> orderService.realiseMessage(buyerId, sellerId, quantity);
                    case "call" -> orderService.realiseCall(buyerId, sellerId);
                    case "service" -> orderService.realiseService(buyerId, sellerId, productName, quantity);
                }
            }
        }

        return ResponseEntity.ok("Received");
    }
}
