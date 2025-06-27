package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.LocationDto;
import com.example.demo.model.Rental;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.RentalService;
import com.example.demo.services.UserManager;
import com.example.demo.services.VehicleService;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/vehicles")
@EnableScheduling
@AllArgsConstructor
public class VehicleController {
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);
    private final VehicleService vehicleService;
    private final UserRepository userRepository;
    private final RentalService rentalService;

    @GetMapping
    public List<Vehicle> getAll() {
        return vehicleService.findAll();
    }

    @GetMapping("/active")
    public List<Vehicle> getAllActive() {
        return vehicleService.findAllActive();
    }

    @DeleteMapping("/delete/{id}")
    public void softDelete(
        @PathVariable String id,
        @AuthenticationPrincipal UserDetails userDetails) {
            String login = userDetails.getUsername();
            User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found: " + login));
            if (UserManager.isAdmin(user)) {
                vehicleService.deleteById(id);
            }else{
                logger.warn("User {} attempted to delete vehicle with ID: {} without sufficient permissions", login, id);
                throw new SecurityException("You do not have permission to delete this vehicle.");
            }
            
    }

    @GetMapping("/available")
    public List<Vehicle> getAvailableVehicles() {
        return vehicleService.findAvailableVehicles();
    }

    @GetMapping("/rented")
    public List<Vehicle> getRentedVehicles(@AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("User not found: " + login));
        if (UserManager.isAdmin(user)) {
            return vehicleService.findRentedVehicles();
        }else{
            logger.warn("User {} attempted to access rented vehicles without sufficient permissions", login);
            throw new SecurityException("You do not have permission to view rented vehicles.");
        }
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        logger.info("Request received for vehicle with ID: {}", id);
        return vehicleService.findById(id)
            .map(vehicle -> {
            return ResponseEntity.ok(vehicle);
            })
            .orElseGet(() -> {
                return ResponseEntity.notFound().build();
            });
    }

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(
        @RequestBody Vehicle vehicle, 
        @AuthenticationPrincipal UserDetails userDetails) {
            String login = userDetails.getUsername();
            User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found: " + login));
            if (UserManager.isAdmin(user)) {
                try {
                    Vehicle savedVehicle = vehicleService.save(vehicle);
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }} else {
                    logger.warn("User {} attempted to add a vehicle without sufficient permissions", login);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
    }

    @PutMapping("/location")
    public ResponseEntity<?> setLocation(@RequestBody LocationDto locationDto, @AuthenticationPrincipal UserDetails userDetails){
        String login = userDetails.getUsername();
        User user = userRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("User not found: " + login));
        Vehicle vehicle = vehicleService.findById(locationDto.getVehicleId()).orElseThrow();
        Optional <Rental> rentalOpt = rentalService.findActiveRentalByVehicleId(vehicle.getId());
        if (rentalOpt.isPresent()){
            Rental rental = rentalOpt.get();
            if (rental.getUserId() == user.getId()){
                vehicle.setLatitude(locationDto.getLatitude());
                vehicle.setLongitude(locationDto.getLongitude());
                vehicleService.save(vehicle);
                return ResponseEntity.ok("Location was updated");
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have access!");
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rental not found");
        }
    }

    @Scheduled(fixedRate = 20000)
    public void setLocationSchedule(){
        List<Vehicle> vehicles = vehicleService.findAll();
        for (Vehicle vehicle : vehicles) {
            vehicle.setLatitude(randomLocation(vehicle.getLatitude()));
            vehicle.setLongitude(randomLocation(vehicle.getLongitude()));
            vehicleService.save(vehicle);
        }
    }

    private double randomLocation(double current){
        return current + offset();
    }

    private double offset(){
        double maxOffset = 0.0005;
        return (Math.random() * 2 - 1) * maxOffset;
    }

    @GetMapping("/admin/vehicles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Vehicle> getVehiclesForAdmin() {
        return vehicleService.findAllActive(); 
    }

    @GetMapping("/locations")
    public ResponseEntity<List<LocationDto>> getVehicleLocations() {
        List<Vehicle> vehicles = vehicleService.findAllActive(); 

        List<LocationDto> locations = vehicles.stream()
            .map(v -> new LocationDto(v.getId(), v.getLatitude(), v.getLongitude()))
            .toList();

        return ResponseEntity.ok(locations);
    }
}
