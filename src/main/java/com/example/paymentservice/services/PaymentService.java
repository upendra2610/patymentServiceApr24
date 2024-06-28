package com.example.paymentservice.services;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentGatwaySelector paymentGatwaySelector;

    public PaymentService(PaymentGatwaySelector paymentGatwaySelector){
        this.paymentGatwaySelector = paymentGatwaySelector;
    }

    public String generatepayment() {
        return paymentGatwaySelector.getPaymentGateway().generatePaymentLink();
    }
}
