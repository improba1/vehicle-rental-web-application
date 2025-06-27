package com.example.demo.services;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;

public class UserManager {
    private final RoleRepository roleRepository;
    private static Role admin; 

    public UserManager(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
        admin = roleRepository.findByName("ADMIN").get();
    }

    public static boolean isAdmin(User user){
        for (Role role : user.getRoles()) {
            if(role.getName().equals(admin.getName())){
                return true;
            }
        }
        return false;
    }
}
