package com.example.demo.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.model.Rental;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.RentalRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.services.RentalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class RentalServiceImpl implements RentalService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final VehicleServiceImpl vehicleService;
    private final UserRepository userRepository;

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndActiveTrue(vehicleId)
                .map(rental -> true)
                .orElse(false);
    }

    @Override
    public Optional<Rental> findActiveRentalByVehicleId(String vehicleId) {
       return rentalRepository.findByVehicleIdAndActiveTrue(vehicleId);
    }

    @Override
    @Transactional
    public Rental rent(String vehicleId, String userId) {
        if (!vehicleService.isAvailable(vehicleId)) {
            throw new IllegalStateException("Vehicle " + vehicleId + " is not available for rent.");
        }
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
        .orElseThrow(() -> new EntityNotFoundException("Vehicle consistency error. ID: " + vehicleId));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                return new EntityNotFoundException("User not found with ID: " + userId);
            });
        Rental newRental = Rental.builder()
            .vehicle(vehicle)
            .user(user)
            .active(true)
            .rentDate(LocalDateTime.now())
            .returnDate(null)
            .build();
        vehicle.setRented(true);
        vehicleRepository.save(vehicle);
        Rental savedRental = rentalRepository.save(newRental);
        return savedRental;
    }


    @Override
    public boolean returnRental(String vehicleId, String userId) {
        Optional<Rental> rentalOpt = rentalRepository.findByVehicleIdAndActiveTrue(vehicleId);
        if (rentalOpt.isEmpty()){
            return false;
        }

        Rental rental = rentalOpt.get();
        if(!rental.getUserId().equals(userId)){
            return false;
        }
        rental.setActive(false);
        rental.setReturnDate(LocalDateTime.now());
        rentalRepository.save(rental);
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow();
        vehicle.setRented(false);
        vehicleRepository.save(vehicle);
        return true;
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
    
}
