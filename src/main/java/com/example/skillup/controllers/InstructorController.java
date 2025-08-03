package com.example.skillup.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.skillup.config.JwtUtils;
import com.example.skillup.dto.ApiResponseDto;
import com.example.skillup.dto.CourseAnalyticsDto;
import com.example.skillup.dto.CourseCreateDto;
import com.example.skillup.dto.CourseDetailDto;
import com.example.skillup.dto.InstructorStatsDto;
import com.example.skillup.dto.LessonCreateDto;
import com.example.skillup.dto.ModuleCreateDto;
import com.example.skillup.models.Course;
import com.example.skillup.models.Enrollment;
import com.example.skillup.models.Lesson;
import com.example.skillup.models.Module;
import com.example.skillup.models.User;
import com.example.skillup.services.CourseService;
import com.example.skillup.services.EnrollmentService;
import com.example.skillup.services.LessonService;
import com.example.skillup.services.ModuleService;
import com.example.skillup.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private ModuleService moduleService;
    
    @Autowired
    private LessonService lessonService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    
    private Long getInstructorIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        
        String token = authHeader.substring(7);
        String username = jwtUtils.extractUsername(token);
        
        if (username == null) {
            throw new RuntimeException("Invalid JWT token");
        }
        
        User user = userService.findByEmail(username).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        if (!"INSTRUCTOR".equals(user.getRole())) {
            throw new RuntimeException("Access denied. Instructor role required.");
        }
        
        return user.getId();
    }
    
   
    
    @PostMapping("/courses")
    public ResponseEntity<ApiResponseDto<Course>> createCourse(
            @Valid @RequestBody CourseCreateDto courseDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            Course course = courseService.createCourse(courseDto, instructorId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(true, "Course created successfully", course));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/courses")
    public ResponseEntity<ApiResponseDto<Page<Course>>> getMyCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> courses = courseService.getCoursesByInstructor(instructorId, pageable);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Courses retrieved successfully", courses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponseDto<CourseDetailDto>> getCourseDetails(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            
            if (!courseService.isInstructorOwner(courseId, instructorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "You are not authorized to view this course", null));
            }
            
            CourseDetailDto courseDetail = courseService.getCourseDetailById(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course details retrieved successfully", courseDetail));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponseDto<Course>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseCreateDto courseDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            Course updatedCourse = courseService.updateCourse(courseId, courseDto, instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course updated successfully", updatedCourse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponseDto<String>> deleteCourse(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            courseService.deleteCourse(courseId, instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course deleted successfully", "Deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<ApiResponseDto<Module>> createModule(
            @PathVariable Long courseId,
            @Valid @RequestBody ModuleCreateDto moduleDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            
            // Verify ownership
            if (!courseService.isInstructorOwner(courseId, instructorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "You are not authorized to modify this course", null));
            }
            
            Module module = moduleService.createModule(moduleDto, courseId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(true, "Module created successfully", module));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<ApiResponseDto<List<Module>>> getCourseModules(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            
            // Verify ownership
            if (!courseService.isInstructorOwner(courseId, instructorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "You are not authorized to view this course", null));
            }
            
            List<Module> modules = moduleService.getModulesByCourseId(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Modules retrieved successfully", modules));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<ApiResponseDto<Module>> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleCreateDto moduleDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            Module updatedModule = moduleService.updateModule(moduleId, moduleDto, instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Module updated successfully", updatedModule));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<ApiResponseDto<String>> deleteModule(
            @PathVariable Long moduleId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            moduleService.deleteModule(moduleId, instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Module deleted successfully", "Deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<ApiResponseDto<Lesson>> createLesson(
            @PathVariable Long moduleId,
            @Valid @RequestBody LessonCreateDto lessonDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            Lesson lesson = lessonService.createLesson(lessonDto, moduleId, instructorId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseDto<>(true, "Lesson created successfully", lesson));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<ApiResponseDto<List<Lesson>>> getModuleLessons(
            @PathVariable Long moduleId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            List<Lesson> lessons = lessonService.getLessonsByModuleId(moduleId, instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lessons retrieved successfully", lessons));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponseDto<Lesson>> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonCreateDto lessonDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            Lesson updatedLesson = lessonService.updateLesson(lessonId, lessonDto, instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lesson updated successfully", updatedLesson));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponseDto<String>> deleteLesson(
            @PathVariable Long lessonId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            lessonService.deleteLesson(lessonId, instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lesson deleted successfully", "Deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponseDto<InstructorStatsDto>> getDashboardStats(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            InstructorStatsDto stats = getInstructorStats(instructorId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Dashboard stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/courses/{courseId}/enrollments")
    public ResponseEntity<ApiResponseDto<Page<Enrollment>>> getCourseEnrollments(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            
            // Verify ownership
            if (!courseService.isInstructorOwner(courseId, instructorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "You are not authorized to view this course data", null));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId, pageable);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course enrollments retrieved successfully", enrollments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    @GetMapping("/courses/{courseId}/analytics")
    public ResponseEntity<ApiResponseDto<CourseAnalyticsDto>> getCourseAnalytics(
            @PathVariable Long courseId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long instructorId = getInstructorIdFromToken(authHeader);
            
            // Verify ownership
            if (!courseService.isInstructorOwner(courseId, instructorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponseDto<>(false, "You are not authorized to view this course analytics", null));
            }
            
            CourseAnalyticsDto analytics = getCourseAnalyticsData(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course analytics retrieved successfully", analytics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
    
    // Helper methods for instructor analytics
    
    private InstructorStatsDto getInstructorStats(Long instructorId) {
        InstructorStatsDto stats = new InstructorStatsDto();
        
        // Get instructor's courses
        List<Course> courses = courseService.getCoursesByInstructorId(instructorId);
        stats.setTotalCourses(courses.size());
        
        // Calculate total enrollments and active students
        int totalEnrollments = 0;
        int totalActiveStudents = 0;
        int totalCompletedCourses = 0;
        
        for (Course course : courses) {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(course.getId());
            totalEnrollments += enrollments.size();
            
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getEnrolledAt() != null) {
                    totalActiveStudents++;
                }
                if (enrollment.getCompletionDate() != null) {
                    totalCompletedCourses++;
                }
            }
        }
        
        stats.setTotalStudents(totalEnrollments);
        stats.setActiveStudents(totalActiveStudents);
        stats.setCompletedCourses(totalCompletedCourses);
        
        return stats;
    }
    
    private CourseAnalyticsDto getCourseAnalyticsData(Long courseId) {
        CourseAnalyticsDto analytics = new CourseAnalyticsDto();
        
        // Get course enrollments
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        analytics.setTotalEnrollments(enrollments.size());
        
        int completedCount = 0;
        int activeCount = 0;
        
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCompletionDate() != null) {
                completedCount++;
            } else if (enrollment.getEnrolledAt() != null) {
                activeCount++;
            }
        }
        
        analytics.setCompletedStudents(completedCount);
        analytics.setActiveStudents(activeCount);
        
        // Calculate completion rate
        if (!enrollments.isEmpty()) {
            analytics.setCompletionRate((double) completedCount / enrollments.size() * 100);
        } else {
            analytics.setCompletionRate(0.0);
        }
        
        return analytics;
    }
    
}
