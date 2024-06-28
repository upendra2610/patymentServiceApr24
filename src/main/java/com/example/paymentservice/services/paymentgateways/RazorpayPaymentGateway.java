package com.example.paymentservice.services.paymentgateways;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RazorpayPaymentGateway implements PaymentGateway{

    private final RazorpayClient razorpayClient;

    public RazorpayPaymentGateway(RazorpayClient razorpayClient){
        this.razorpayClient = razorpayClient;
    }

    @Override
    public String generatePaymentLink() {
        try{
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", 100000);
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("accept_partial", false);

//            paymentLinkRequest.put("first_min_partial_amount", 100);
        paymentLinkRequest.put("expire_by", Instant.now().getEpochSecond()+1800);
        paymentLinkRequest.put("reference_id", "TS1909868ACFGH7H075255");
        paymentLinkRequest.put("description", "Payment for policy no #23456");

        JSONObject customer = new JSONObject();
        customer.put("name", "+919340026196");
        customer.put("contact", "Gaurav Kumar");
        customer.put("email", "us5587991@gmail.com");
        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("sms", true);
        notify.put("email", true);
        paymentLinkRequest.put("notify", notify);
//            paymentLinkRequest.put("reminder_enable", true);

//            JSONObject notes = new JSONObject();
//            notes.put("policy_name", "Jeevan Bima");
//            paymentLinkRequest.put("notes", notes);
        paymentLinkRequest.put("callback_url", "https://www.google.com/");
        paymentLinkRequest.put("callback_method", "get");

        PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);

        return payment.get("short_url").toString();
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;

    }
}
