package com.example.skillup.controllers;

import java.util.List;

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
import com.example.skillup.dto.CourseCreateDto;
import com.example.skillup.dto.CourseDetailDto;
import com.example.skillup.dto.LessonCreateDto;
import com.example.skillup.dto.ModuleCreateDto;
import com.example.skillup.models.Course;
import com.example.skillup.models.Lesson;
import com.example.skillup.models.Module;
import com.example.skillup.models.User;
import com.example.skillup.services.CourseService;
import com.example.skillup.services.LessonService;
import com.example.skillup.services.ModuleService;
import com.example.skillup.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
@Validated
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private ModuleService moduleService;
    
    @Autowired
    private LessonService lessonService;
    
    @Autowired
    private UserService userService;
    

    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email).orElse(null);
    }

    
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<Course>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Course> coursePage = courseService.getCoursesPaginated(pageable);
            
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Courses retrieved successfully", 
                coursePage.getContent()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving courses: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponseDto<CourseDetailDto>> getCourseDetails(@PathVariable Long courseId) {
        try {
            CourseDetailDto courseDetails = courseService.getCourseDetails(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Course details retrieved successfully", 
                courseDetails
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving course details: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponseDto<List<Course>>> getCoursesByCategory(@PathVariable String category) {
        try {
            List<Course> courses = courseService.getCoursesByCategory(category);
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Courses in category '" + category + "' retrieved successfully", 
                courses
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving courses by category: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<List<Course>>> searchCourses(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, "Search keyword cannot be empty", null));
            }
            
            List<Course> courses = courseService.searchCourses(keyword.trim());
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Search completed successfully", 
                courses
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error searching courses: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponseDto<List<String>>> getAllCategories() {
        try {
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
    
    @PostMapping
    public ResponseEntity<ApiResponseDto<Course>> createCourse(
            @Valid @RequestBody CourseCreateDto courseDto, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            if (!"INSTRUCTOR".equals(instructor.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, "Only instructors can create courses", null));
            }
            
            Course course = courseService.createCourse(courseDto, instructor.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Course created successfully", course));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error creating course: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/instructor/my-courses")
    public ResponseEntity<ApiResponseDto<List<Course>>> getInstructorCourses(Authentication authentication) {
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            List<Course> courses = courseService.getCoursesByInstructor(instructor.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Instructor courses retrieved successfully", 
                courses
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving instructor courses: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponseDto<Course>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseCreateDto courseDto, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            Course course = courseService.updateCourse(courseId, courseDto, instructor.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course updated successfully", course));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error updating course: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponseDto<String>> deleteCourse(
            @PathVariable Long courseId, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            courseService.deleteCourse(courseId, instructor.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Course deleted successfully", "Deleted"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error deleting course: " + e.getMessage(), null));
        }
    }
    
    @PostMapping("/{courseId}/modules")
    public ResponseEntity<ApiResponseDto<Module>> createModule(
            @PathVariable Long courseId,
            @Valid @RequestBody ModuleCreateDto moduleDto, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            // Set the course ID from path
            moduleDto.setCourseId(courseId);
            
            Module module = moduleService.createModule(moduleDto, instructor.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Module created successfully", module));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error creating module: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{courseId}/modules")
    public ResponseEntity<ApiResponseDto<List<Module>>> getCourseModules(@PathVariable Long courseId) {
        try {
            List<Module> modules = moduleService.getModulesByCourse(courseId);
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Course modules retrieved successfully", 
                modules
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving course modules: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<ApiResponseDto<Module>> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleCreateDto moduleDto, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            Module module = moduleService.updateModule(moduleId, moduleDto, instructor.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Module updated successfully", module));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error updating module: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<ApiResponseDto<String>> deleteModule(
            @PathVariable Long moduleId, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            moduleService.deleteModule(moduleId, instructor.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Module deleted successfully", "Deleted"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error deleting module: " + e.getMessage(), null));
        }
    }

    
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<ApiResponseDto<Lesson>> createLesson(
            @PathVariable Long moduleId,
            @Valid @RequestBody LessonCreateDto lessonDto, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            // Set the module ID from path
            lessonDto.setModuleId(moduleId);
            
            Lesson lesson = lessonService.createLesson(lessonDto, instructor.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(true, "Lesson created successfully", lesson));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error creating lesson: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<ApiResponseDto<List<Lesson>>> getModuleLessons(@PathVariable Long moduleId) {
        try {
            List<Lesson> lessons = lessonService.getLessonsByModule(moduleId);
            return ResponseEntity.ok(new ApiResponseDto<>(
                true, 
                "Module lessons retrieved successfully", 
                lessons
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error retrieving module lessons: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponseDto<Lesson>> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonCreateDto lessonDto, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            Lesson lesson = lessonService.updateLesson(lessonId, lessonDto, instructor.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lesson updated successfully", lesson));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error updating lesson: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponseDto<String>> deleteLesson(
            @PathVariable Long lessonId, 
            Authentication authentication) {
        
        try {
            User instructor = getAuthenticatedUser(authentication);
            
            if (instructor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, "Instructor not found", null));
            }
            
            lessonService.deleteLesson(lessonId, instructor.getId());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "Lesson deleted successfully", "Deleted"));
                
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(false, "Error deleting lesson: " + e.getMessage(), null));
        }
    }
}
