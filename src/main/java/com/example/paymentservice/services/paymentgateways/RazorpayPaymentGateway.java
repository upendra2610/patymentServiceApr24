package com.example.paymentservice.services.paymentgateways;

import com.example.paymentservice.modals.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RazorpayPaymentGateway implements PaymentGateway{

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    public RazorpayPaymentGateway(RazorpayClient razorpayClient, PaymentRepository paymentRepository){
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String generatePaymentLink() {
        try{
            //All the details will get from order service but here hardcoding all
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", 100000);
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("accept_partial", false);

//            paymentLinkRequest.put("first_min_partial_amount", 100);
        paymentLinkRequest.put("expire_by", Instant.now().getEpochSecond()+1800);
        paymentLinkRequest.put("reference_id", "T19999008801331109");
        paymentLinkRequest.put("description", "Payment for policy no #23456");

        JSONObject customer = new JSONObject();
        customer.put("name", "+919340026196");
        customer.put("contact", "Gaurav Kumar");
        customer.put("email", "us5587991@gmail.com");
        paymentLinkRequest.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("sms", true);
//        notify.put("whatsapp", true);
        notify.put("email", true);
        paymentLinkRequest.put("notify", notify);

//            paymentLinkRequest.put("upi_link", true); // works only in live mode not in test mode
//            paymentLinkRequest.put("reminder_enable", true);

//            JSONObject notes = new JSONObject();
//            notes.put("policy_name", "Jeevan Bima");
//            paymentLinkRequest.put("notes", notes);
        paymentLinkRequest.put("callback_url", "https://www.google.com/");
        paymentLinkRequest.put("callback_method", "get");

        PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
            Payment payment1 = new Payment();
            payment1.setAmount(10L);
            payment1.setGateway("razorpay");
            payment1.setCurrency(payment.get("currency"));
            payment1.setStatus("pending");
//            payment1.setTransactionId(payment.get("id"));
            paymentRepository.save(payment1);

        return payment.toString();
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;

    }
}
