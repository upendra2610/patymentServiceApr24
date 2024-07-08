package com.example.paymentservice.repository;

import com.example.paymentservice.modals.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment save(Payment Payment);
    Payment findPaymentByIdIs(String id);
}
