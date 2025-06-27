package com.example.demo.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Role {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    private String name;  
    private boolean active = true;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users;
}
