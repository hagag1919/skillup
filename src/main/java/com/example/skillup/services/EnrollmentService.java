package com.example.skillup.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.skillup.models.Course;
import com.example.skillup.models.Enrollment;
import com.example.skillup.models.User;
import com.example.skillup.repo.CourseRepository;
import com.example.skillup.repo.EnrollmentRepository;
import com.example.skillup.repo.UserRepository;

@Service
@Transactional
public class EnrollmentService {
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    public Enrollment enrollUser(Long userId, Long courseId) {
        // Check if user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        
        User user = userOpt.get();
        if (!"STUDENT".equals(user.getRole())) {
            throw new RuntimeException("Only students can enroll in courses");
        }
        
        // Check if course exists
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        // Check if already enrolled
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("User is already enrolled in this course");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setProgress(0.0f);
        
        return enrollmentRepository.save(enrollment);
    }
    
    public List<Enrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }
    
    public List<Enrollment> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
    
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return getCourseEnrollments(courseId);
    }
    
    public Page<Enrollment> getEnrollmentsByCourseId(Long courseId, Pageable pageable) {
        return enrollmentRepository.findByCourseId(courseId, pageable);
    }
    
    public Optional<Enrollment> getEnrollment(Long userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
    }
    
    public Enrollment updateProgress(Long userId, Long courseId, Float progress) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setProgress(progress);
        
        // Mark as completed if progress is 100%
        if (progress >= 100.0f && enrollment.getCompletionDate() == null) {
            enrollment.setCompletionDate(LocalDate.now());
        }
        
        return enrollmentRepository.save(enrollment);
    }
    
    public void unenrollUser(Long userId, Long courseId) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        
        enrollmentRepository.delete(enrollmentOpt.get());
    }
    
    public boolean isUserEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }
    
    public List<Enrollment> getCompletedEnrollments(Long userId) {
        return enrollmentRepository.findByUserIdAndCompletionDateIsNotNull(userId);
    }
    
    public List<Enrollment> getInProgressEnrollments(Long userId) {
        return enrollmentRepository.findByUserIdAndCompletionDateIsNull(userId);
    }
    
    public Map<String, Object> getEnrollmentAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Total enrollments
        long totalEnrollments = enrollmentRepository.count();
        analytics.put("totalEnrollments", totalEnrollments);
        
        // Completed enrollments
        long completedEnrollments = enrollmentRepository.countCompletedEnrollments();
        analytics.put("completedEnrollments", completedEnrollments);
        
        // In-progress enrollments
        long inProgressEnrollments = enrollmentRepository.countInProgressEnrollments();
        analytics.put("inProgressEnrollments", inProgressEnrollments);
        
        // Completion rate
        double completionRate = totalEnrollments > 0 ? (double) completedEnrollments / totalEnrollments * 100 : 0;
        analytics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        
        // Enrollment growth data - placeholder for monthly data
        List<Map<String, Object>> enrollmentsByMonth = new ArrayList<>();
        Map<String, Object> currentMonth = new HashMap<>();
        currentMonth.put("month", "Current");
        currentMonth.put("count", totalEnrollments);
        enrollmentsByMonth.add(currentMonth);
        analytics.put("enrollmentsByMonth", enrollmentsByMonth);
        
        // Enrollment growth - placeholder for daily data
        List<Map<String, Object>> enrollmentGrowth = new ArrayList<>();
        Map<String, Object> currentDay = new HashMap<>();
        currentDay.put("date", java.time.LocalDate.now().toString());
        currentDay.put("count", totalEnrollments);
        enrollmentGrowth.add(currentDay);
        analytics.put("enrollmentGrowth", enrollmentGrowth);
        
        // Most popular courses - converting Object[] to proper structure
        List<Object[]> popularCoursesRaw = enrollmentRepository.findMostPopularCourses();
        List<Map<String, Object>> topCourses = new ArrayList<>();
        for (Object[] course : popularCoursesRaw) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("courseId", course[0]);
            courseData.put("enrollments", course[1]);
            // We would need to join with Course table to get title, for now using courseId
            courseData.put("courseTitle", "Course " + course[0]);
            topCourses.add(courseData);
        }
        analytics.put("topCourses", topCourses);
        
        // For backward compatibility
        analytics.put("popularCourses", popularCoursesRaw);
        
        // Users with most enrollments
        List<Object[]> activeUsers = enrollmentRepository.findUsersWithMostEnrollments();
        analytics.put("activeUsers", activeUsers);
        
        return analytics;
    }
    
    public Double getCourseCompletionRate(Long courseId) {
        return enrollmentRepository.getCompletionRateByCourse(courseId);
    }
    
    public Double getCourseAverageProgress(Long courseId) {
        return enrollmentRepository.getAverageProgressByCourse(courseId);
    }
}
