package com.example.demo.repository;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
     boolean existsByVehicleIdAndReturnDateIsNull(String vehicleId);
     Optional<Rental> findByVehicleIdAndActiveTrue(String vehicleId);
     Set<Rental> findByVehicleRentedTrue();
}
