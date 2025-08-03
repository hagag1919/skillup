package com.example.skillup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.skillup.config.JwtUtils;
import com.example.skillup.dto.ApiResponseDto;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid authorization header"));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtils.isTokenValid(token)) {
                String email = jwtUtils.extractUsername(token);
                Long userId = jwtUtils.extractUserId(token);
                String role = jwtUtils.extractRole(token);
                String name = jwtUtils.extractName(token);
                
                TokenValidationResponse response = new TokenValidationResponse(userId, email, name, role);
                return ResponseEntity.ok(ApiResponseDto.success("Token is valid", response));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Token is invalid or expired"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Token validation failed: " + e.getMessage()));
        }
    }
    
    
    public static class TokenValidationResponse {
        private Long userId;
        private String email;
        private String name;
        private String role;
        
        public TokenValidationResponse(Long userId, String email, String name, String role) {
            this.userId = userId;
            this.email = email;
            this.name = name;
            this.role = role;
        }
        
        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
