package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    List<Role> findByActiveTrue(); 
    List<Role> findByActiveFalse();
    List<Role> findAll();
    Optional<Role> findById(String id); 
    void deleteById(String id); 
    Optional<Role> findByName(String name);
}
