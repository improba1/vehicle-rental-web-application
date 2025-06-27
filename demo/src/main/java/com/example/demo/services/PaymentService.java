package com.example.demo.services;

public interface PaymentService {
    String createCheckoutSession(String rentalId);
    void handleWebhook(String payload, String signature);

}
