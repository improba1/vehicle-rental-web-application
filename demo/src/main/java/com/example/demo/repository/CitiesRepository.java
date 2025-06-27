package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.City;

@Repository
public interface CitiesRepository extends JpaRepository<City, String> {
    List<City> findAll();
    Optional<City> findByName(String name); 
    void deleteByName(String name); 
}
