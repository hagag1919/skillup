package com.example.skillup.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    // =============== ADMIN METHODS ===============
    
    public Page<User> getAllUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Page<User> getUsersByRole(String role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    public User createUser(UserRegistrationDto userDto) {
        return registerUser(userDto);
    }
    
    public User updateUserByAdmin(Long userId, UserRegistrationDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Admin can update all fields including email and role
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Email is already taken!");
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getRole() != null && isValidAdminRole(userDto.getRole())) {
            user.setRole(userDto.getRole());
        }
        if (userDto.getBio() != null) {
            user.setBio(userDto.getBio());
        }
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public Map<String, Object> getSystemAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        long totalUsers = userRepository.count();
        long studentCount = countUsersByRole("STUDENT");
        long instructorCount = countUsersByRole("INSTRUCTOR");
        long adminCount = countUsersByRole("ADMIN");
        
        analytics.put("totalUsers", totalUsers);
        analytics.put("studentCount", studentCount);
        analytics.put("instructorCount", instructorCount);
        analytics.put("adminCount", adminCount);
        
        // User growth this month (you'd need to add date filtering to repository)
        analytics.put("newUsersThisMonth", 0); // Placeholder
        
        return analytics;
    }
    
    public Map<String, Object> getUserAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalUsers", userRepository.count());
        analytics.put("students", countUsersByRole("STUDENT"));
        analytics.put("instructors", countUsersByRole("INSTRUCTOR"));
        analytics.put("admins", countUsersByRole("ADMIN"));
        
        // Active users (users with enrollments or created courses)
        analytics.put("usersWithCourses", userRepository.findUsersWithCreatedCourses().size());
        
        return analytics;
    }
    
    private boolean isValidAdminRole(String role) {
        return "STUDENT".equals(role) || "INSTRUCTOR".equals(role) || "ADMIN".equals(role);
    }
}
