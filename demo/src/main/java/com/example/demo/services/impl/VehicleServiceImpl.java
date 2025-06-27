package com.example.demo.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Vehicle;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.services.VehicleService;


@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAllActive() {
        return vehicleRepository.findByActiveTrue();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

   @Override
    @Transactional
    public Vehicle save(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().isBlank()) {
            vehicle.setId(UUID.randomUUID().toString());
            vehicle.setActive(true);
        }
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return savedVehicle;
    }


    @Override
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findByRentedFalse();
    }

    @Override
    public List<Vehicle> findRentedVehicles() {
        return vehicleRepository.findByRentedTrue();
    }

    @Override
    public boolean isAvailable(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .map(vehicle -> !vehicle.isRented())
                .orElse(false);
    }

    @Override
    public void deleteById(String id) {
        vehicleRepository.findById(id).ifPresent(vehicle -> {
            vehicle.setActive(false);
            vehicleRepository.save(vehicle);
        });
    }
    
}
