package com.example.skillup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.skillup.config.JwtUtils;
import com.example.skillup.dto.ApiResponseDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping("/validate")
    public ResponseEntity<ApiResponseDto<TokenValidationResponse>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(false, "Invalid authorization header", null));
            }
            
            String token = authHeader.substring(7);
            
            if (jwtUtils.isTokenValid(token)) {
                String email = jwtUtils.extractUsername(token);
                Long userId = jwtUtils.extractUserId(token);
                String role = jwtUtils.extractRole(token);
                String name = jwtUtils.extractName(token);
                
                TokenValidationResponse response = new TokenValidationResponse(userId, email, name, role);
                return ResponseEntity.ok(new ApiResponseDto<>(true, "Token is valid", response));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(false, "Token is invalid or expired", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Token validation failed: " + e.getMessage(), null));
        }
    }
    
    
    public static class TokenValidationResponse {
        private Long id;
        private String email;
        private String name;
        private String role;
        private String bio;
        private String createdAt;
        private String updatedAt;
        
        public TokenValidationResponse(Long id, String email, String name, String role) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.role = role;
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }
}
