package com.example.demo.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
public class Rental {
    public Rental(){
        this.active = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private User user;
    private LocalDateTime rentDate;
    private LocalDateTime returnDate;

    @OneToOne(mappedBy = "rental")
    private Payment payment;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    
    public String getUserId() {
        return user != null ? user.getId() : null;
    }
}
