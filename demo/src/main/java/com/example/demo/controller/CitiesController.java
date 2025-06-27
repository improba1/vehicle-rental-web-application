package com.example.demo.controller;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.City;
import com.example.demo.model.User;
import com.example.demo.repository.CitiesRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.UserManager;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/cities")
@AllArgsConstructor
public class CitiesController {
    private final UserRepository userRepository;
    private final CitiesRepository citiesRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addCity(
        @RequestBody City city, 
        @AuthenticationPrincipal UserDetails userDetails) {
            String login = userDetails.getUsername();
            User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found: " + login));
            if (UserManager.isAdmin(user)) {
                Optional<City> c = citiesRepository.findByName(city.getName());
                if (c.isPresent() && c.get().is_active() == true){
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("City " + city.getName() + " is already in the database"); 
                }else if (c.isPresent() && c.get().is_active() == false){
                    c.get().set_active(true);
                    citiesRepository.save(c.get());
                    return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("City " + c.get().getName() + " is in the database again");
                }
                try {
                    City savedCity = citiesRepository.save(city);
                    return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("City " + savedCity.getName() + " added to the database");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }} else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have access");
            }
    }

     @DeleteMapping("/delete/{name}")
    public ResponseEntity<?> softDelete(
        @PathVariable String name,
        @AuthenticationPrincipal UserDetails userDetails) {
            String login = userDetails.getUsername();
            User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found: " + login));
            if (UserManager.isAdmin(user)){
                City city = citiesRepository.findByName(name).orElseThrow();
                city.set_active(false);
                citiesRepository.save(city);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have access");
            }
    }
}
