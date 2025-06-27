package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Vehicle;

public interface VehicleService {
    List<Vehicle> findAll();
    List<Vehicle> findAllActive();
    Optional<Vehicle> findById(String id);
    Vehicle save(Vehicle vehicle);
    List<Vehicle> findAvailableVehicles();
    List<Vehicle> findRentedVehicles();
    boolean isAvailable(String vehicleId);
    void deleteById(String id);//soft delete
    
}
