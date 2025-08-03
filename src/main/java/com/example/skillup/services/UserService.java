package com.example.skillup.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.skillup.dto.UserRegistrationDto;
import com.example.skillup.models.User;
import com.example.skillup.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(UserRegistrationDto registrationDto) {
        
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }
        
        
        if (!isValidRole(registrationDto.getRole())) {
            throw new RuntimeException("Role must be either STUDENT or INSTRUCTOR");
        }
        
        
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(registrationDto.getRole());
        user.setBio(registrationDto.getBio());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getAllInstructors() {
        return userRepository.findByRole("INSTRUCTOR");
    }
    
    public List<User> getAllStudents() {
        return userRepository.findByRole("STUDENT");
    }
    
    public User updateUser(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        
        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }
        if (userDetails.getBio() != null) {
            user.setBio(userDetails.getBio());
        }
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }
    
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<User> getUsersWithCreatedCourses() {
        return userRepository.findUsersWithCreatedCourses();
    }
    
    public List<User> getUsersEnrolledInCourse(Long courseId) {
        return userRepository.findUsersEnrolledInCourse(courseId);
    }
    
    private boolean isValidRole(String role) {
        return "STUDENT".equals(role) || "INSTRUCTOR".equals(role);
    }
    
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    public long getStudentCount() {
        return countUsersByRole("STUDENT");
    }
    
    public long getInstructorCount() {
        return countUsersByRole("INSTRUCTOR");
    }
}
