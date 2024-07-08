package com.example.paymentservice.modals;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity

public class Payment {
    @Id
    private String id;
    private String gateway;
//    private String transactionId;
    private Long amount;
    private String paymentIntentId;
//    private String paymentLinkId;
    private String currency;
    private String status;
}
