package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "app_cities")
public class City {
    private boolean is_active = true;
    public City(){
        this.is_active = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
}
