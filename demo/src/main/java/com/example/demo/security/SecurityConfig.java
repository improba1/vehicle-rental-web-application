package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration 
@EnableWebSecurity 
@EnableMethodSecurity 
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http, 
        JwtAuthFilter jwtAuthFilter,
        AuthenticationProvider authProvider) throws Exception {
            http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth 
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/api/user/**").authenticated()
            .requestMatchers("/api/auth/**").permitAll() 
            .requestMatchers("/admin_map.html").permitAll()
            .requestMatchers("api/vehicles**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN") 
            .requestMatchers("/api/payments/webhook").permitAll()
            .requestMatchers("/api/payments/success").permitAll()
            .requestMatchers("/api/payments/cancel").permitAll()
            .anyRequest().authenticated() 
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }


    @Bean
    public AuthenticationProvider authenticationProvider(
        UserDetailsService userDetailsService, 
        PasswordEncoder passwordEncoder){
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); 
            provider.setUserDetailsService(userDetailsService); 
            provider.setPasswordEncoder(passwordEncoder); 
            return provider;
        }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); 
    }
}