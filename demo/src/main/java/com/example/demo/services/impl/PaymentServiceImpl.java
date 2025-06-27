package com.example.demo.services.impl;

import com.example.demo.enums.PaymentStatus;
import com.example.demo.model.Payment;
import com.example.demo.model.Rental;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.RentalRepository;
import com.example.demo.services.PaymentService;
import com.example.demo.services.RentalService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stripe.model.checkout.Session;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RentalService rentalService;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;

    @Value("${STRIPE_API_KEY}")
    private String apiKey;

    @Value("${WEBHOOK_SECRET}")
    private String webhookSecret;

    @Override
    @Transactional
    public String createCheckoutSession(String rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + rentalId));

        Stripe.apiKey = apiKey;

        double rentalPrice = calculateRentalPrice(rental);
        long amount = Math.round(rentalPrice * 100);

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Rental " + rentalId)
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("pln")
                        .setUnitAmount(amount)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putMetadata("rentalId", rentalId)
                .setSuccessUrl("http://localhost:8080/api/payments/success")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .build();

        try {
            Session session = Session.create(params);
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .amount(rentalPrice)
                    .createdAt(LocalDateTime.now())
                    .rental(rental)
                    .stripeSessionId(session.getId())
                    .status(PaymentStatus.PENDING)
                    .build();
            Payment savedP = paymentRepository.save(payment);
            rental.setPayment(savedP);
            rentalRepository.save(rental);
            return session.getUrl();
        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        Stripe.apiKey = apiKey;
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature", e);
        }

        if ("checkout.session.completed".equals(event.getType())) {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
            String sessionId = ((Session) stripeObject).getId();

            if (sessionId != null) {
                paymentRepository.findByStripeSessionId(sessionId).ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.PAID);
                    payment.setPaidAt(LocalDateTime.now());
                    paymentRepository.save(payment);

                    Rental rental = payment.getRental();
                    rentalService.returnRental(rental.getVehicle().getId(), rental.getUser().getId());
                });
            }
        }
    }

    public double calculateRentalPrice(Rental rental) {
        if (rental == null || rental.getRentDate() == null || rental.getVehicle().getId() == null) {
            throw new IllegalArgumentException("Invalid rental or missing data.");
        }

        LocalDate rentDate;
        LocalDate returnDate;

        try {
            rentDate = rental.getRentDate().toLocalDate();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid rent date format: " + rental.getRentDate(), e);
        }

        if (rental.getReturnDate() == null) {
            returnDate = LocalDate.now();
        } else {
            try {
                returnDate = rental.getReturnDate().toLocalDate();
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid return date format: " + rental.getReturnDate(), e);
            }
        }

        long rentalDays = ChronoUnit.DAYS.between(rentDate, returnDate);


        if (Duration.between(rentDate.atStartOfDay(), returnDate.atStartOfDay()).toHours() % 24 > 0) {
            rentalDays += 1;
        }

        rentalDays += 1;

        rentalDays = Math.max(rentalDays, 1);

        double dailyPrice = rental.getVehicle().getPrice();
        return dailyPrice * rentalDays;
    }
}

