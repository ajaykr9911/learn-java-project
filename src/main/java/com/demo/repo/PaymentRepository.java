package com.demo.repo;

import com.demo.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);
    boolean existsByOrderIdAndStatus(String orderId, String status);
    Optional<Payment> findByOrderIdAndStatus(String orderId, String status);

}
