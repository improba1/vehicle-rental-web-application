package com.example.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.LocationDto;
import com.example.demo.dto.UserDto;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VehicleRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminController {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/vehicles/locations")
    public List<LocationDto> getAllVehicleLocations() {
        return vehicleRepository.findAll().stream()
                .map(vehicle -> new LocationDto(vehicle.getId(), vehicle.getLatitude(), vehicle.getLongitude()))
                .toList();
    }

    @DeleteMapping("delete/user/{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable String id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(false);
        userRepository.save(user);
        return ResponseEntity.ok("User soft-deleted");
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getLogin(), user.getPassword(), user.getRoles(), user.getAddress(), user.isActive()))
                .toList();
    }

    @PostMapping("/user/{id}/grant")
    public ResponseEntity<?> grantRole(@PathVariable String id, @RequestParam String role) {
        User user = userRepository.findById(id).orElseThrow();
        Role r = roleRepository.findByName(role.toUpperCase()).orElseThrow();
        user.getRoles().add(r);
        userRepository.save(user);
        return ResponseEntity.ok("Role granted");
    }

    @PostMapping("/user/{id}/revoke")
    public ResponseEntity<?> revokeRole(@PathVariable String id, @RequestParam String role) {
        User user = userRepository.findById(id).orElseThrow();
        Role r = roleRepository.findByName(role.toUpperCase()).orElseThrow();
        user.getRoles().remove(r);
        userRepository.save(user);
        return ResponseEntity.ok("Role revoked");
    }
}