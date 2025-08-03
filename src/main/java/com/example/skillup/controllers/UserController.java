package com.example.skillup.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skillup.dto.ApiResponseDto;
import com.example.skillup.dto.JwtResponseDto;
import com.example.skillup.dto.LoginDto;
import com.example.skillup.dto.UserRegistrationDto;
import com.example.skillup.dto.UserStatsDto;
import com.example.skillup.models.User;
import com.example.skillup.services.UserService;
import com.example.skillup.utils.InputValidation;
import com.example.skillup.config.JwtUtils;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        try {
            
            InputValidation.ValidationResult validationResult = InputValidation.validateUserRegistration(
                registrationDto.getName(),
                registrationDto.getEmail(),
                registrationDto.getPassword(),
                registrationDto.getRole(),
                registrationDto.getBio()
            );
            
            if (!validationResult.isValid()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Validation failed: " + validationResult.getErrors()));
            }
            
            User user = userService.registerUser(registrationDto);
            
            
            UserResponseDto userResponse = new UserResponseDto(user);
            
            return ResponseEntity.ok(ApiResponseDto.success("User registered successfully!", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        try {
        
            InputValidation.ValidationResult validationResult = InputValidation.validateUserLogin(
                loginDto.getEmail(),
                loginDto.getPassword()
            );
            
            if (!validationResult.isValid()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Validation failed: " + validationResult.getErrors()));
            }
            
            Optional<User> userOptional = userService.findByEmail(loginDto.getEmail());
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("User not found"));
            }
            
            User user = userOptional.get();
            
            // Check password
            if (!userService.checkPassword(loginDto.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid password"));
            }
            
            // Generate JWT token
            String jwtToken = jwtUtils.generateTokenForUser(user);
            
            JwtResponseDto response = new JwtResponseDto(jwtToken, 
                    user.getId(), user.getEmail(), user.getName(), user.getRole());
            
            return ResponseEntity.ok(ApiResponseDto.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Login failed: " + e.getMessage()));
        }
    }
    
    // User management endpoints
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            // Validate ID parameter
            if (!InputValidation.isValidId(id)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid user ID"));
            }
            
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                UserResponseDto userResponse = new UserResponseDto(user.get());
                return ResponseEntity.ok(ApiResponseDto.success("User retrieved successfully", userResponse));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve user: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users/instructors")
    public ResponseEntity<?> getAllInstructors() {
        try {
            List<User> instructors = userService.getAllInstructors();
            List<UserResponseDto> instructorResponses = instructors.stream()
                    .map(UserResponseDto::new)
                    .toList();
            return ResponseEntity.ok(ApiResponseDto.success("Instructors retrieved successfully", instructorResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve instructors: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<User> students = userService.getAllStudents();
            List<UserResponseDto> studentResponses = students.stream()
                    .map(UserResponseDto::new)
                    .toList();
            return ResponseEntity.ok(ApiResponseDto.success("Students retrieved successfully", studentResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve students: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String name) {
        try {
            // Validate search query
            if (!InputValidation.isValidSearchQuery(name)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid search query. Search term is required and must not contain special characters"));
            }
            
            // Sanitize the search input
            String sanitizedName = InputValidation.sanitizeString(name);
            
            List<User> users = userService.searchUsersByName(sanitizedName);
            List<UserResponseDto> userResponses = users.stream()
                    .map(UserResponseDto::new)
                    .toList();
            return ResponseEntity.ok(ApiResponseDto.success("Search completed successfully", userResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Search failed: " + e.getMessage()));
        }
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            // Validate ID parameter
            if (!InputValidation.isValidId(id)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid user ID"));
            }
            
            // Validate user details
            if (userDetails.getName() != null && !InputValidation.isValidName(userDetails.getName())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid name format"));
            }
            
            if (userDetails.getBio() != null && !InputValidation.isValidBio(userDetails.getBio())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid bio content"));
            }
            
            // Sanitize inputs before processing
            if (userDetails.getName() != null) {
                userDetails.setName(InputValidation.sanitizeString(userDetails.getName()));
            }
            if (userDetails.getBio() != null) {
                userDetails.setBio(InputValidation.sanitizeString(userDetails.getBio()));
            }
            
            User updatedUser = userService.updateUser(id, userDetails);
            UserResponseDto userResponse = new UserResponseDto(updatedUser);
            return ResponseEntity.ok(ApiResponseDto.success("User updated successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("User update failed: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // Validate ID parameter
            if (!InputValidation.isValidId(id)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid user ID"));
            }
            
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponseDto.success("User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("User deletion failed: " + e.getMessage()));
        }
    }
    
    // Statistics endpoints
    @GetMapping("/users/stats/role/{role}")
    public ResponseEntity<?> getUserCountByRole(@PathVariable String role) {
        try {
            // Validate role parameter
            if (!InputValidation.isValidRole(role)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponseDto.error("Invalid role. Role must be STUDENT or INSTRUCTOR"));
            }
            
            // Sanitize role input
            String sanitizedRole = InputValidation.sanitizeString(role).toUpperCase();
            
            long count = userService.countUsersByRole(sanitizedRole);
            return ResponseEntity.ok(ApiResponseDto.success("User count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve user count: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users/stats")
    public ResponseEntity<?> getUserStatistics() {
        try {
            UserStatsDto stats = new UserStatsDto();
            stats.setTotalUsers(userService.getTotalUsers());
            stats.setStudentCount(userService.getStudentCount());
            stats.setInstructorCount(userService.getInstructorCount());
            
            return ResponseEntity.ok(ApiResponseDto.success("User statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to retrieve user statistics: " + e.getMessage()));
        }
    }
    
    // Helper classes for responses
    public static class UserResponseDto {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String bio;
        
        public UserResponseDto(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.bio = user.getBio();
        }
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }
    
  
}
