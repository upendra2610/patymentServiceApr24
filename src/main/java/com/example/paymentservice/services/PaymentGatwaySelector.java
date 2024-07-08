package com.example.paymentservice.services;

import com.example.paymentservice.services.paymentgateways.PaymentGateway;
import com.example.paymentservice.services.paymentgateways.RazorpayPaymentGateway;
import com.example.paymentservice.services.paymentgateways.StripeGateway;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatwaySelector {
    private final RazorpayPaymentGateway razorpayGateway;
    private final StripeGateway stripeGateway;
    private final PaymentGatewayHealthCheckService paymentGatewayHealthCheckService;

    public PaymentGatwaySelector(
            RazorpayPaymentGateway razorpayGateway,
            StripeGateway stripeGateway,
            PaymentGatewayHealthCheckService paymentGatewayHealthCheckService
    ){
        this.razorpayGateway = razorpayGateway;
        this.stripeGateway = stripeGateway;
        this.paymentGatewayHealthCheckService = paymentGatewayHealthCheckService;
    }

    public PaymentGateway getPaymentGateway(){
        if(paymentGatewayHealthCheckService.isStripeHealthy()){
            return stripeGateway;
        }
        else if (paymentGatewayHealthCheckService.isRazorpayHealthy()){
            return stripeGateway;
        }
        else {
            throw new RuntimeException("No payment gateway is currently available");
        }

    }
}
