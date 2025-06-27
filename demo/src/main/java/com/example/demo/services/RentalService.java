package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Rental;

public interface RentalService {
    boolean isVehicleRented(String vehicleId);
    Optional<Rental> findActiveRentalByVehicleId(String vehicleId);
    Rental rent(String vehicleId, String userId);
    boolean returnRental(String vehicleId, String userId);
    List<Rental> findAll();
}
