package com.example.paymentservice.controllers;

import com.example.paymentservice.services.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }
    @PostMapping("/payments")
    public String initiatePayment() {
        return paymentService.generatepayment();
    }
}
