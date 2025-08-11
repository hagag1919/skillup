package com.example.skillup.services;

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

import com.example.skillup.dto.CourseCreateDto;
import com.example.skillup.dto.CourseDetailDto;
import com.example.skillup.models.Course;
import com.example.skillup.models.Lesson;
import com.example.skillup.models.Module;
import com.example.skillup.models.User;
import com.example.skillup.repo.CourseRepository;
import com.example.skillup.repo.EnrollmentRepository;
import com.example.skillup.repo.ModuleRepository;
import com.example.skillup.repo.UserRepository;

@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    public Course createCourse(CourseCreateDto courseDto, Long instructorId) {

        Optional<User> instructorOpt = userRepository.findById(instructorId);
        if (instructorOpt.isEmpty()) {
            throw new RuntimeException("Instructor not found with ID: " + instructorId);
        }
        
        User instructor = instructorOpt.get();
        if (!"INSTRUCTOR".equals(instructor.getRole())) {
            throw new RuntimeException("User is not an instructor");
        }
        
        Course course = new Course();
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setCategory(courseDto.getCategory());
        course.setThumbnailUrl(courseDto.getThumbnailUrl());
        course.setInstructorId(instructorId);
        
        return courseRepository.save(course);
    }
    
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public Page<Course> getCoursesPaginated(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
    
    public List<Course> getCoursesByCategory(String category) {
        return courseRepository.findByCategoryIgnoreCase(category);
    }
    
    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }
    
    public Page<Course> getCoursesByInstructor(Long instructorId, Pageable pageable) {
        return courseRepository.findByInstructorId(instructorId, pageable);
    }
    
    public List<Course> getCoursesByInstructorId(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }
    
    public CourseDetailDto getCourseDetailById(Long courseId) {
        return getCourseDetails(courseId);
    }
    
    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }
    
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }
    
    public CourseDetailDto getCourseDetails(Long courseId) {
        // First, get the course with modules (one collection fetch)
        Optional<Course> courseOpt = courseRepository.findByIdWithModules(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        
        // Then, get modules with lessons (second collection fetch)
        List<Module> modulesWithLessons = moduleRepository.findByCourseIdWithLessons(courseId);
        
        // Set the fetched modules with lessons to the course
        course.setModules(modulesWithLessons);
        
        CourseDetailDto detailDto = new CourseDetailDto();
        

        detailDto.setId(course.getId());
        detailDto.setTitle(course.getTitle());
        detailDto.setDescription(course.getDescription());
        detailDto.setCategory(course.getCategory());
        detailDto.setThumbnailUrl(course.getThumbnailUrl());
        detailDto.setInstructorId(course.getInstructorId());
        

        if (course.getInstructor() != null) {
            detailDto.setInstructorName(course.getInstructor().getName());
        }
        

        List<Module> modules = course.getModules();
        if (modules != null) {
            detailDto.setTotalModules(modules.size());
            
            int totalLessons = 0;
            int totalDurationMinutes = 0;
            
            for (Module module : modules) {
                if (module.getLessons() != null) {
                    totalLessons += module.getLessons().size();
                    for (Lesson lesson : module.getLessons()) {
                        if (lesson.getDurationSeconds() != null) {
                            totalDurationMinutes += lesson.getDurationSeconds() / 60;
                        }
                    }
                }
            }
            
            detailDto.setTotalLessons(totalLessons);
            detailDto.setTotalDurationMinutes(totalDurationMinutes);
        }
        

        Long enrolledCount = enrollmentRepository.countByCourseId(courseId);
        detailDto.setEnrolledStudents(enrolledCount.intValue());
        
        return detailDto;
    }
    
    public Course updateCourse(Long courseId, CourseCreateDto courseDto, Long instructorId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        

        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("You are not authorized to update this course");
        }
        
        course.setTitle(courseDto.getTitle());
        course.setDescription(courseDto.getDescription());
        course.setCategory(courseDto.getCategory());
        course.setThumbnailUrl(courseDto.getThumbnailUrl());
        
        return courseRepository.save(course);
    }
    
    public void deleteCourse(Long courseId, Long instructorId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        
        // Verify ownership
        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("You are not authorized to delete this course");
        }
        

        Long enrollmentCount = enrollmentRepository.countByCourseId(courseId);
        if (enrollmentCount > 0) {
            throw new RuntimeException("Cannot delete course with enrolled students");
        }
        
        courseRepository.delete(course);
    }
    
    public boolean isInstructorOwner(Long courseId, Long instructorId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        return courseOpt.isPresent() && courseOpt.get().getInstructorId().equals(instructorId);
    }
    
    public List<String> getAllCategories() {
        return courseRepository.findDistinctCategories();
    }
    
    // Admin methods for course management
    public void featureCourse(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        course.setFeatured(true);
        courseRepository.save(course);
    }
    
    public void unfeatureCourse(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        course.setFeatured(false);
        courseRepository.save(course);
    }
    
    public void activateCourse(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        course.setActive(true);
        courseRepository.save(course);
    }
    
    public void deactivateCourse(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        course.setActive(false);
        courseRepository.save(course);
    }
    
    public void deleteCourseByAdmin(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        
        // Admin can delete course even if it has enrollments (force delete)
        // This is different from instructor delete which checks for enrollments
        courseRepository.delete(course);
    }
    
    public Map<String, Object> getCourseAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Total courses
        long totalCourses = courseRepository.count();
        analytics.put("totalCourses", totalCourses);
        
        // Active courses
        long activeCourses = courseRepository.countByActive(true);
        analytics.put("activeCourses", activeCourses);
        
        // Featured courses
        long featuredCourses = courseRepository.countByFeatured(true);
        analytics.put("featuredCourses", featuredCourses);
        
        // Courses by category
        List<String> categories = courseRepository.findDistinctCategories();
        List<Map<String, Object>> coursesByCategory = new ArrayList<>();
        for (String category : categories) {
            long count = courseRepository.countByCategory(category);
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("category", category);
            categoryData.put("count", count);
            coursesByCategory.add(categoryData);
        }
        analytics.put("coursesByCategory", coursesByCategory);
        
        // Total enrollments across all courses
        long totalEnrollments = enrollmentRepository.count();
        analytics.put("totalEnrollments", totalEnrollments);
        
        // Average enrollments per course
        double avgEnrollments = totalCourses > 0 ? (double) totalEnrollments / totalCourses : 0;
        analytics.put("averageEnrollmentsPerCourse", Math.round(avgEnrollments * 100.0) / 100.0);
        
        // Course creation data - placeholder for monthly data
        List<Map<String, Object>> courseCreationData = new ArrayList<>();
        Map<String, Object> currentMonth = new HashMap<>();
        currentMonth.put("month", "Current");
        currentMonth.put("count", totalCourses);
        courseCreationData.add(currentMonth);
        analytics.put("courseCreationData", courseCreationData);
        
        return analytics;
    }
}
