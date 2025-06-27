package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminMapController {

    @GetMapping("/admin_map")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminMapPage() {
        return "admin_map";  // вернёт шаблон admin_map.html из templates
    }
}