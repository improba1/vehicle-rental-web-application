package com.example.demo.model;

import com.example.demo.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@ToString(exclude = "rental")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @OneToOne
    @JoinColumn(name = "rental_id", nullable = false)
    @JsonIgnore
    private Rental rental;

    @Column(columnDefinition = "NUMERIC")
    private double amount;

    @Column(name = "stripe_session_id", unique = true)
    private String stripeSessionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;


}