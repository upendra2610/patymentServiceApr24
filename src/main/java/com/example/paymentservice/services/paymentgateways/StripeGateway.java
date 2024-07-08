package com.example.paymentservice.services.paymentgateways;

import com.example.paymentservice.modals.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeGateway implements PaymentGateway {
    @Value("${stripe.api.key}")
    private String apiKey;

    private final PaymentRepository paymentRepository;

    public StripeGateway(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String generatePaymentLink() {
        Stripe.apiKey = apiKey;

        SessionCreateParams sessionParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://www.google.com/")
                .setCancelUrl("https://leetcode.com/")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(1000L) // amount in cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Product name")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                ).build();


        PriceCreateParams priceCreateParams = PriceCreateParams.builder()
                .setCurrency("inr")
                .setUnitAmount(100000L)
                .setProductData(
                        PriceCreateParams.ProductData.builder().setName("Gold").build()
                )
                .build();
        Price price = null;
        try {
            price = Price.create(priceCreateParams);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        PaymentLinkCreateParams params =
                PaymentLinkCreateParams.builder()
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(price.getId())
                                        .build()
                        )
                        .setAfterCompletion(
                                PaymentLinkCreateParams.AfterCompletion.builder()
                                        .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                        .setRedirect(
                                                PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                                        .setUrl("https://google.com?payment_id={CHECKOUT_SESSION_ID}")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();


        PaymentLink paymentLink = null;
        try {
            Session session = Session.create(sessionParams);
            String checkoutSessionId = session.getId();
            Session retrievedSession = Session.retrieve(checkoutSessionId);
            String paymentIntentId = retrievedSession.getPaymentIntent();
            paymentLink = PaymentLink.create(params);

            Payment payment = new Payment();
            payment.setId(checkoutSessionId);
            payment.setPaymentIntentId(paymentIntentId);
            payment.setGateway("stripe");
            payment.setStatus("pending");
            payment.setAmount(priceCreateParams.getUnitAmount());
//            payment.setTransactionId(paymentLink.getId());
            payment.setCurrency(paymentLink.getCurrency());
            paymentRepository.save(payment);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return paymentLink.toString();
    }
}
//        // Step 1: Create Checkout Session
//        SessionCreateParams sessionParams = SessionCreateParams.builder()
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl("https://www.google.com/")
//                .setCancelUrl("https://leetcode.com/")
//                .addLineItem(
//                        SessionCreateParams.LineItem.builder()
//                                .setQuantity(1L)
//                                .setPriceData(
//                                        SessionCreateParams.LineItem.PriceData.builder()
//                                                .setCurrency("usd")
//                                                .setUnitAmount(1000L) // amount in cents
//                                                .setProductData(
//                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                                .setName("Product name")
//                                                                .build()
//                                                )
//                                                .build()
//                                )
//                                .build()
//                ).build();
//
//        PriceCreateParams priceCreateParams = PriceCreateParams.builder()
//                .setCurrency("inr")
//                .setUnitAmount(100000L)
//                .setProductData(
//                        PriceCreateParams.ProductData.builder().setName("Gold").build()
//                )
//                .build();
//        Price price = null;
//        try {
//            price = Price.create(priceCreateParams);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        try {
//            Session session = Session.create(sessionParams);
//            String checkoutSessionId = session.getId();
//            String paymentIntentId = session.getPaymentIntent(); // Ensure you have a way to get this ID
//
//            // Step 2: Create Payment Link
//            PaymentLinkCreateParams params =
//                    PaymentLinkCreateParams.builder()
//                            .addLineItem(
//                                    PaymentLinkCreateParams.LineItem.builder()
//                                            .setQuantity(1L)
//                                            .setPrice(price.getId())
//                                            .build()
//                            )
//                            .setAfterCompletion(
//                                    PaymentLinkCreateParams.AfterCompletion.builder()
//                                            .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
//                                            .setRedirect(
//                                                    PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
//                                                            .setUrl("https://google.com?payment_id={CHECKOUT_SESSION_ID}")
//                                                            .build()
//                                            )
//                                            .build()
//                            )
//                            .build();
//
//            PaymentLink paymentLink = PaymentLink.create(params);
//            String paymentLinkId = paymentLink.getId();
//
//            // Step 3: Save Transaction
//            Payment transaction = new Payment();
//            transaction.setId(checkoutSessionId); // Use checkout session ID as primary key
//            transaction.setStatus("pending");
////            transaction.setCustomerEmail(customerEmail);
//            transaction.setPaymentIntentId(paymentIntentId);
//            transaction.setPaymentLinkId(paymentLinkId);
//
//            paymentRepository.save(transaction);
//            return paymentLink.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
////        }
//    }
//}


//        CustomerCreateParams customerCreateParams = CustomerCreateParams.builder(
//        SessionCreateParams sessionCreateParams =
//                SessionCreateParams.builder()
//                        .setSuccessUrl("https://example.com/success")
//                        .addLineItem(
//                                SessionCreateParams.LineItem.builder()
//                                        .setPrice("price_1MotwRLkdIwHu7ixYcPLm5uZ")
//                                        .setQuantity(2L)
//                                        .build()
//                        )
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .build();
//        Session session = Session.create(sessionCreateParams);

////actual code i have implemented
//
//
//    }
//}
//PriceCreateParams priceCreateParams = PriceCreateParams.builder()
//        .setCurrency("inr")
//        .setUnitAmount(100000L)
//        .setProductData(
//                PriceCreateParams.ProductData.builder().setName("Gold").build()
//        )
//        .build();
//Price price = null;
//        try {
//price = Price.create(priceCreateParams);
//        } catch (Exception e) {
//        System.out.println(e.getMessage());
//        }
//
//
//PaymentLinkCreateParams params =
//        PaymentLinkCreateParams.builder()
//                .addLineItem(
//                        PaymentLinkCreateParams.LineItem.builder()
//                                .setQuantity(1L)
//                                .setPrice(price.getId())
//                                .build()
//                )
//                .setAfterCompletion(
//                        PaymentLinkCreateParams.AfterCompletion.builder()
//                                .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
//                                .setRedirect(
//                                        PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
//                                                .setUrl("https://google.com?payment_id={CHECKOUT_SESSION_ID}")
//                                                .build()
//                                )
//                                .build()
//                )
//                .build();
//
//
//
//PaymentLink paymentLink = null;
//        try {
//paymentLink = PaymentLink.create(params);
//        } catch (Exception e) {
//        System.out.println(e.getMessage());
//        }
//Payment payment = new Payment();
//        payment.setId(paymentLink.getId());
//        payment.setGateway("stripe");
//        payment.setStatus("pending");
//        payment.setAmount(priceCreateParams.getUnitAmount());
//        payment.setTransactionId(paymentLink.getId());
//        payment.setCurrency(paymentLink.getCurrency());
//        paymentRepository.save(payment);
//
//        return paymentLink.toString();


//        // Set your secret key. Remember to switch to your live secret key in production.
//// See your keys here: https://dashboard.stripe.com/apikeys
//        Stripe.apiKey = apiKey;
////        Map<String, Object> recurring = new HashMap<>();
////        recurring.put("interval", "month");
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("unit_amount", 100000L);
//        params.put("currency", "inr");
////        params.put("recurring", recurring);
////        params.put("product", "prod_P2eyHe1yYpImlF");
//
//        Map<String, Object> productData = new HashMap<>();
//        productData.put("name", "Burnol");
//        params.put("product_data", productData);
//
//        Price price = null;
//        try {
//            price = Price.create(params);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        List<Object> lineItems = new ArrayList<>();
//        Map<String, Object> lineItem1 = new HashMap<>();
//        lineItem1.put(
//                "price",
//                price.getId()
//        );
//        lineItem1.put("quantity", 1);
//        lineItems.add(lineItem1);
//        params = new HashMap<>();
//        params.put("line_items", lineItems);
//
//        Map<String, Object> afterCompletion = new HashMap<>();
//        afterCompletion.put("type", "redirect");
//
//        Map<String, Object> redirect = new HashMap<>();
//        redirect.put("url", "https://scaler.com?payment_id={CHECKOUT_SESSION_ID}");
////        afterCompletion.put("redirect.url", "https://scaler.com");
//
//        afterCompletion.put("redirect", redirect);
//
//        params.put("after_completion", afterCompletion);
//
//        PaymentLink paymentLink = null;
//        try {
//            paymentLink = PaymentLink.create(params);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        return paymentLink.toString();
//    }
//}
//
