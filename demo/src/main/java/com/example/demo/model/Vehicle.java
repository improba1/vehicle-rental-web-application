package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@AllArgsConstructor
@Getter
public class Vehicle {
    public Vehicle(){}
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private double price;
    private int year;
    private String model;
    private String brand;
    private String extra;
    private String category;
    private String type;
    private boolean active = true;
    private boolean rented;

    private double latitude;
    private double longitude;


}
