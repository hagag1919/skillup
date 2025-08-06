package com.example.skillup.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.skillup.dto.ApiResponseDto;
import com.example.skillup.dto.CourseProgressDto;
import com.example.skillup.dto.EnrollmentResponseDto;
import com.example.skillup.dto.StudentDashboardDto;
import com.example.skillup.models.Enrollment;
import com.example.skillup.models.Lesson;
import com.example.skillup.models.Module;
import com.example.skillup.models.Progress;
import com.example.skillup.models.User;
import com.example.skillup.services.StudentService;
import com.example.skillup.services.UserService;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private UserService userService;
    
    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        
        if (user == null) {
            return null;
        }
        
        // Ensure user has STUDENT role or is an INSTRUCTOR who can also be a student
        if (!"STUDENT".equals(user.getRole()) && !"INSTRUCTOR".equals(user.getRole())) {
            return null;
        }
        
        return user;
    }
    
    // =============== ENROLLMENT ENDPOINTS ===============
    
    @PostMapping("/enroll/{courseId}")
    public ResponseEntity<ApiResponseDto<Enrollment>> enrollInCourse(
            @PathVariable Long courseId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            Enrollment enrollment = studentService.enrollInCourse(user.getId(), courseId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Successfully enrolled in course", enrollment));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error enrolling in course: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/unenroll/{courseId}")
    public ResponseEntity<ApiResponseDto<String>> unenrollFromCourse(
            @PathVariable Long courseId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            studentService.unenrollFromCourse(user.getId(), courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Successfully unenrolled from course", "Unenrolled"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error unenrolling from course: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponseDto<List<EnrollmentResponseDto>>> getUserEnrollments(
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            List<EnrollmentResponseDto> enrollments = studentService.getUserEnrollments(user.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Enrollments retrieved successfully", enrollments));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving enrollments: " + e.getMessage(), null));
        }
    }
    
    // =============== PROGRESS TRACKING ENDPOINTS ===============
    
    @GetMapping("/courses/{courseId}/progress")
    public ResponseEntity<ApiResponseDto<CourseProgressDto>> getCourseProgress(
            @PathVariable Long courseId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            CourseProgressDto progress = studentService.getCourseProgress(user.getId(), courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course progress retrieved successfully", progress));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving course progress: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<ApiResponseDto<Progress>> markLessonComplete(
            @PathVariable Long lessonId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            Progress progress = studentService.markLessonComplete(user.getId(), lessonId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lesson marked as complete", progress));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error marking lesson complete: " + e.getMessage(), null));
        }
    }
    
    // =============== COURSE CONTENT ACCESS ENDPOINTS ===============
    
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<ApiResponseDto<List<Module>>> getCourseModulesForStudent(
            @PathVariable Long courseId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            List<Module> modules = studentService.getCourseModulesForStudent(user.getId(), courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course modules retrieved successfully", modules));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving course modules: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<ApiResponseDto<List<Lesson>>> getModuleLessonsForStudent(
            @PathVariable Long moduleId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            List<Lesson> lessons = studentService.getModuleLessonsForStudent(user.getId(), moduleId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Module lessons retrieved successfully", lessons));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving module lessons: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponseDto<Lesson>> getLessonForStudent(
            @PathVariable Long lessonId,
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            Lesson lesson = studentService.getLessonForStudent(user.getId(), lessonId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lesson retrieved successfully", lesson));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving lesson: " + e.getMessage(), null));
        }
    }
    
    // =============== DASHBOARD ENDPOINT ===============
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponseDto<StudentDashboardDto>> getStudentDashboard(
            Authentication authentication) {
        try {
            User user = getAuthenticatedUser(authentication);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Authentication required", null));
            }
            
            StudentDashboardDto dashboard = studentService.getStudentDashboard(user.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Student dashboard retrieved successfully", dashboard));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving student dashboard: " + e.getMessage(), null));
        }
    }
}
