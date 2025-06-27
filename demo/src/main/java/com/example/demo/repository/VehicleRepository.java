package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByActiveTrue();
    Optional<Vehicle> findByIdAndActiveTrue(String id);
    List<Vehicle> findByActiveTrueAndIdNotIn(Set<String> rentedVehicleIds);
    List<Vehicle> findByRentedTrue();
    List<Vehicle> findByRentedFalse();
}
