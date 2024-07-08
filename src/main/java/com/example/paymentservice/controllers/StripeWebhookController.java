package com.example.paymentservice.controllers;

import com.example.paymentservice.modals.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String stripeSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PaymentRepository paymentRepository;

    public StripeWebhookController(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
//        // Verify the webhook signature
//        try {
//            Event event = Webhook.constructEvent(payload, sigHeader, stripeSecret);
//            JsonNode jsonNode = objectMapper.readTree(payload);
//            String transactionId = extractTransactionId(jsonNode);
//
//            // Handle the event
//            switch (event.getType()) {
//                case "payment_intent.succeeded":
//                    // Handle payment succeeded
//                    System.out.println(event.getId());
//                    break;
//                case "payment_intent.payment_failed":
//                    // Handle payment failed
//                    break;
//                // Add more cases for other event types as needed
//                default:
//                    // Handle unknown event type
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown event type");
//            }
//
//            // Response to Stripe
//            return ResponseEntity.status(HttpStatus.OK).body("Received");
//        } catch (SignatureVerificationException e) {
//            // Invalid signature
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
//        } catch (JsonMappingException e) {
//            throw new RuntimeException(e);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        Event event;

        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, stripeSecret
            );

            JsonNode jsonNode = objectMapper.readTree(payload);
//            String transactionId = extractTransactionId(jsonNode);

            // Process the transactionId as needed
//            System.out.println("Transaction ID: " + transactionId);

             //Handle the event
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntenetSucceeded(jsonNode);
                    // Handle payment succeeded
//                    System.out.println(event.getId());
                    break;
                case "payment_intent.payment_failed":
                    // Handle payment failed
                    break;
                // Add more cases for other event types as needed
                default:
                    // Handle unknown event type
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown event type");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }

        return ResponseEntity.ok("Webhook received");
    }

//    private String extractTransactionId(JsonNode jsonNode) {
//        // Adjust the path based on the actual structure of the Stripe event payload
//        return jsonNode.at("/data/object/id").asText();
//    }
    private void handlePaymentIntenetSucceeded(JsonNode jsonNode){
//        JsonNode jsonNode = objectMapper.convertValue(event.getData().getObject(), JsonNode.class);
//        JsonNode paymentIntent = jsonNode.at("/data/object");
//        String checkoutSessionId = paymentIntent.get("id").asText();
//        String transactionId = paymentIntent.get("payment_intent").asText();
//            JsonNode jsonNode = objectMapper.readTree(event.toJson());
            JsonNode session = jsonNode.at("/data/object");
            String checkoutSessionId = session.get("id").asText();
            String paymentIntentId = session.get("payment_intent").asText();
            System.out.println(checkoutSessionId + " " + paymentIntentId);

            Payment payment = paymentRepository.findPaymentByIdIs(checkoutSessionId);
            payment.setStatus("succeeded");
            paymentRepository.save(payment);

    }
    }

