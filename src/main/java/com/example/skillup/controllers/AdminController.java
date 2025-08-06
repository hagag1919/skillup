package com.example.skillup.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
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
import com.example.skillup.dto.PaginatedResponseDto;
import com.example.skillup.dto.UserRegistrationDto;
import com.example.skillup.models.Course;
import com.example.skillup.models.User;
import com.example.skillup.services.CertificateService;
import com.example.skillup.services.CourseService;
import com.example.skillup.services.EnrollmentService;
import com.example.skillup.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private CertificateService certificateService;
    
    private User getAuthenticatedAdmin(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElse(null);
        
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return null;
        }
        return user;
    }
    
    private <T> PaginatedResponseDto<T> createPaginatedResponse(Page<T> page) {
        return new PaginatedResponseDto<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
    
    // =============== USER MANAGEMENT ===============
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto<PaginatedResponseDto<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String role,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<User> userPage;
            if (role != null && !role.isEmpty()) {
                userPage = userService.getUsersByRole(role, pageable);
            } else {
                userPage = userService.getAllUsersPaginated(pageable);
            }
            
            PaginatedResponseDto<User> paginatedResponse = createPaginatedResponse(userPage);
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Users retrieved successfully", 
                paginatedResponse
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving users: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseDto<User>> getUserById(
            @PathVariable Long userId,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(false, "User not found", null));
            }
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "User retrieved successfully", 
                user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving user: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/users")
    public ResponseEntity<ApiResponseDto<User>> createUser(
            @Valid @RequestBody UserRegistrationDto userDto,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            User newUser = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "User created successfully", newUser));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error creating user: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponseDto<User>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRegistrationDto userDto,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            User updatedUser = userService.updateUserByAdmin(userId, userDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "User updated successfully", updatedUser));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error updating user: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponseDto<String>> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "User deleted successfully", "Deleted"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error deleting user: " + e.getMessage(), null));
        }
    }
    
    
    // =============== COURSE MANAGEMENT ===============
    
    @GetMapping("/courses")
    public ResponseEntity<ApiResponseDto<PaginatedResponseDto<Course>>> getAllCoursesAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Course> coursePage = courseService.getCoursesPaginated(pageable);
            
            PaginatedResponseDto<Course> paginatedResponse = createPaginatedResponse(coursePage);
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Courses retrieved successfully", 
                paginatedResponse
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving courses: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/courses/{courseId}/feature")
    public ResponseEntity<ApiResponseDto<String>> featureCourse(
            @PathVariable Long courseId,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            courseService.featureCourse(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course featured successfully", "Featured"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error featuring course: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/courses/{courseId}/unfeature")
    public ResponseEntity<ApiResponseDto<String>> unfeatureCourse(
            @PathVariable Long courseId,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            courseService.unfeatureCourse(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course unfeatured successfully", "Unfeatured"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error unfeaturing course: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/courses/{courseId}/deactivate")
    public ResponseEntity<ApiResponseDto<String>> deactivateCourse(
            @PathVariable Long courseId,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            courseService.deactivateCourse(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course deactivated successfully", "Deactivated"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error deactivating course: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/courses/{courseId}/activate")
    public ResponseEntity<ApiResponseDto<String>> activateCourse(
            @PathVariable Long courseId,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            courseService.activateCourse(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course activated successfully", "Activated"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error activating course: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponseDto<String>> deleteCourseAdmin(
            @PathVariable Long courseId,
            Authentication authentication) {
        
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }
            
            courseService.deleteCourseByAdmin(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course deleted successfully", "Deleted"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error deleting course: " + e.getMessage(), null));
        }
    }
    
    // =============== ANALYTICS AND REPORTING ===============
    
    @GetMapping("/analytics/overview")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getDashboardOverview(
            Authentication authentication) {
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }

            Map<String, Object> overviewStats = userService.getDashboardOverview();
            return ResponseEntity.ok(new ApiResponseDto<>(
                true,
                "Dashboard overview retrieved successfully",
                overviewStats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving dashboard overview: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/users")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getUserAnalytics(
            Authentication authentication) {
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }

            Map<String, Object> userAnalytics = userService.getUserAnalytics();
            return ResponseEntity.ok(new ApiResponseDto<>(
                true,
                "User analytics retrieved successfully",
                userAnalytics
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving user analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/courses")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getCourseAnalytics(
            Authentication authentication) {
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }

            Map<String, Object> courseAnalytics = courseService.getCourseAnalytics();
            return ResponseEntity.ok(new ApiResponseDto<>(
                true,
                "Course analytics retrieved successfully",
                courseAnalytics
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving course analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/analytics/enrollments")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getEnrollmentAnalytics(
            Authentication authentication) {
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }

            Map<String, Object> enrollmentAnalytics = enrollmentService.getEnrollmentAnalytics();
            return ResponseEntity.ok(new ApiResponseDto<>(
                true,
                "Enrollment analytics retrieved successfully",
                enrollmentAnalytics
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving enrollment analytics: " + e.getMessage(), null));
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<ApiResponseDto<List<User>>> searchUsers(
            @RequestParam String query,
            Authentication authentication) {
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }

            List<User> users = userService.searchUsers(query);
            return ResponseEntity.ok(new ApiResponseDto<>(
                true,
                "Users retrieved successfully",
                users
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving users: " + e.getMessage(), null));
        }
    }

    // =============== CATEGORY MANAGEMENT ===============

    @GetMapping("/courses/categories")
    public ResponseEntity<ApiResponseDto<List<String>>> getAllCategories(Authentication authentication) {
        try {
            User admin = getAuthenticatedAdmin(authentication);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Admin access required", null));
            }

            List<String> categories = courseService.getAllCategories();
            return ResponseEntity.ok(new ApiResponseDto<>(
                true,
                "Categories retrieved successfully",
                categories
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving categories: " + e.getMessage(), null));
        }
    }
}
