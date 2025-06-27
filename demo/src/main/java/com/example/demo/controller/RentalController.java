package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.RentalRequest;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.model.Rental;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.PaymentService;
import com.example.demo.services.RentalService;
import com.example.demo.services.VehicleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;
    private final PaymentService paymentService;

   @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(
        @RequestBody RentalRequest rentalRequest, 
        @AuthenticationPrincipal UserDetails userDetails) {
            if (rentalRequest.vehicleId == null || userDetails.getUsername() == null) {
                return ResponseEntity.badRequest().build();
            }else if(rentalService.isVehicleRented(rentalRequest.vehicleId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is already rented");

            }
            try {
                String username = userDetails.getUsername();
                User user = userRepository.findByLogin(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                Rental rental = rentalService.rent(rentalRequest.vehicleId, user.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(rental);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    @PostMapping("/return")
    public ResponseEntity<?> returnVehicle(
        @RequestBody RentalRequest rentalRequest, 
        @AuthenticationPrincipal UserDetails userDetails) {
            System.out.println("UserDetail = " + userDetails);
            String login = userDetails.getUsername();
            User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Optional<Vehicle> vehicleOpt = vehicleService.findById(rentalRequest.vehicleId);
            if (vehicleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found");
            }
            Vehicle vehicle = vehicleOpt.get();
            
            Optional<Rental> rentalOpt = rentalService.findActiveRentalByVehicleId(rentalRequest.vehicleId);
                if (rentalOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Active rental not found");
                }
            Rental rental = rentalOpt.get();
            
            if (user.getId() != rental.getUser().getId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Error: user not found.");
            }
            if (isInAllowedZone(vehicle)){
                if (rental.getPayment() == null) {
                    String checkoutUrl = paymentService.createCheckoutSession(rental.getId().toString());
                    return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(checkoutUrl);
                }else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("This rental has already been paid and processed.");
                }
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("This vehicle is not in allowed zone!");
            }
        }

    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalService.findAll();
    }

    private boolean isInAllowedZone(Vehicle vehicle){
        //51.251901, 22.570862
            double companyLat = 51.251901;
            double companyLon = 22.570862;
            return distance(vehicle.getLatitude(), vehicle.getLongitude(), companyLat, companyLon) < 0.5; 
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371; 
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c; 
    }
}
