package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Payment;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String>{
    Optional<Payment> findByStripeSessionId(String sessionId);
}