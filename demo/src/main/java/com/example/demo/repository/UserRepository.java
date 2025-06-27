package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByActiveTrue(); 
    List<User> findByActiveFalse();
    List<User> findAll();
    Optional<User> findById(String id); 
    void deleteById(String id); 
    Optional<User> findByLogin(String login);
}