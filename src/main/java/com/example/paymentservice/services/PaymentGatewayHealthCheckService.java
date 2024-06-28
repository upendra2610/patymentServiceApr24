package com.example.paymentservice.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentGatewayHealthCheckService {
    @Value("${strip.api.key}")
    private String stripeApiKey;

    @Value("${razorpay.key.id}")
    private String razorpayApiKey;

    @Value("${razorpay.key.secret}")
    private String razorpayApiSecret;

    private final RestTemplate restTemplate;

    public PaymentGatewayHealthCheckService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public boolean isStripeHealthy() {
        try {
            String url = "https://api.stripe.com/v1/charges";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(stripeApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            return false;
        }
    }

    public boolean isRazorpayHealthy(){
        try {
            String url = "https://api.razorpay.com/v1/payments";
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(razorpayApiKey, razorpayApiSecret);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            return false;
        }
    }

}
