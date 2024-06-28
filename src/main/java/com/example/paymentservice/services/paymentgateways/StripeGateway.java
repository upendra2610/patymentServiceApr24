package com.example.paymentservice.services.paymentgateways;

import com.stripe.Stripe;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeGateway implements PaymentGateway {
    @Value("${stripe.api.key}")
    private String apiKey;

    @Override
    public String generatePaymentLink() {
        Stripe.apiKey = apiKey;


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
            paymentLink = PaymentLink.create(params);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return paymentLink.getUrl();

    }
}


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
