package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapViewController {
    @GetMapping("/admin/map")
    public String adminMap() {
        return "admin_map"; // вернёт шаблон admin_map.html
    }
}