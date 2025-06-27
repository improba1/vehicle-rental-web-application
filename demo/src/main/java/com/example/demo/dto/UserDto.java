package com.example.demo.dto;

import java.util.HashSet;
import java.util.Set;

import com.example.demo.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private String id;
    private String login;
    private String password;
    private Set<Role> roles = new HashSet<>();
    private String address;
    private boolean active;
}
