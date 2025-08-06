package com.example.skillup.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.example.skillup.models.Course;
import com.example.skillup.models.User;
import com.example.skillup.repo.CourseRepository;
import com.example.skillup.repo.EnrollmentRepository;
import com.example.skillup.repo.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
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
    
    public List<User> searchUsers(String query) {
        return userRepository.findByNameContainingIgnoreCase(query);
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
        
        long totalUsers = userRepository.count();
        long students = countUsersByRole("STUDENT");
        long instructors = countUsersByRole("INSTRUCTOR");
        long admins = countUsersByRole("ADMIN");
        
        analytics.put("totalUsers", totalUsers);
        
        // User distribution by role
        Map<String, Long> usersByRole = new HashMap<>();
        usersByRole.put("STUDENT", students);
        usersByRole.put("INSTRUCTOR", instructors);
        usersByRole.put("ADMIN", admins);
        analytics.put("usersByRole", usersByRole);
        
        // Individual counts for backward compatibility
        analytics.put("students", students);
        analytics.put("instructors", instructors);
        analytics.put("admins", admins);
        
        // Active users (users with enrollments or created courses)
        long usersWithCourses = userRepository.findUsersWithCreatedCourses().size();
        analytics.put("usersWithCourses", usersWithCourses);
        
        // User growth data - placeholder for now, can be enhanced with actual date-based queries
        List<Map<String, Object>> userGrowthData = new ArrayList<>();
        Map<String, Object> currentMonth = new HashMap<>();
        currentMonth.put("month", "Current");
        currentMonth.put("count", totalUsers);
        userGrowthData.add(currentMonth);
        analytics.put("userGrowthData", userGrowthData);
        
        // Recent registrations - placeholder
        List<Map<String, Object>> recentRegistrations = new ArrayList<>();
        analytics.put("recentRegistrations", recentRegistrations);
        
        return analytics;
    }
    
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // Fetch total counts
        long totalUsers = userRepository.count();
        long totalStudents = countUsersByRole("STUDENT");
        long totalInstructors = countUsersByRole("INSTRUCTOR");
        
        // Get actual course and enrollment counts
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        
        // Fetch recent users (limit to 5)
        List<User> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc();
        
        // Fetch recent courses (limit to 5)
        List<Course> recentCourses = courseRepository.findTop5ByOrderByCreatedAtDesc();
        
        // Add data to the overview map
        overview.put("totalUsers", totalUsers);
        overview.put("totalStudents", totalStudents);
        overview.put("totalInstructors", totalInstructors);
        overview.put("totalCourses", totalCourses);
        overview.put("totalEnrollments", totalEnrollments);
        overview.put("recentUsers", recentUsers);
        overview.put("recentCourses", recentCourses);
        
        return overview;
    }
    
    private boolean isValidAdminRole(String role) {
        return "STUDENT".equals(role) || "INSTRUCTOR".equals(role) || "ADMIN".equals(role);
    }
}
