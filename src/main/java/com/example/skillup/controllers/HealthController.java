package com.example.skillup.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public String health() {
        return "SkillUp API is running! User functionality is implemented.";
    }
    
    @GetMapping("/")
    public String welcome() {
        return "Welcome to SkillUp E-Learning Platform API";
    }
}
