package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository; 
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getLogin(),
                loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        LoginResponse responseBody = new LoginResponse(token);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest regRequest) {
        if (userRepository.findByLogin(regRequest.getLogin()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with this login already exists.");
        }

        User user = new User();
        user.setLogin(regRequest.getLogin());
        user.setAddress(regRequest.getAddress());
        user.setPassword(passwordEncoder.encode(regRequest.getPassword())); 
        user.getRoles().add(roleRepository.findByName("USER").get());
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }
}