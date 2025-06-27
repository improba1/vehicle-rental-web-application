package com.example.demo.services;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
            .orElseThrow(() -> new UsernameNotFoundException("User" + username + " doesn't exist"));
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User is deactivated");
        }
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(java.util.stream.Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
            user.getLogin(),
            user.getPassword(),
            user.isActive(), 
            true,             
            true,             
            true,             
            authorities
        );
    }
}