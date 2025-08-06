package com.example.skillup.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.skillup.dto.CourseProgressDto;
import com.example.skillup.dto.EnrollmentResponseDto;
import com.example.skillup.dto.StudentDashboardDto;
import com.example.skillup.models.Course;
import com.example.skillup.models.Enrollment;
import com.example.skillup.models.EnrollmentId;
import com.example.skillup.models.Lesson;
import com.example.skillup.models.Module;
import com.example.skillup.models.Progress;
import com.example.skillup.models.User;
import com.example.skillup.repo.CourseRepository;
import com.example.skillup.repo.EnrollmentRepository;
import com.example.skillup.repo.LessonRepository;
import com.example.skillup.repo.ModuleRepository;
import com.example.skillup.repo.ProgressRepository;
import com.example.skillup.repo.UserRepository;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProgressRepository progressRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private LessonRepository lessonRepository;
    
    public Enrollment enrollInCourse(Long userId, Long courseId) {
        // Check if user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if course exists
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));
        
        // Check if already enrolled
        EnrollmentId enrollmentId = new EnrollmentId(userId, courseId);
        if (enrollmentRepository.existsById(enrollmentId)) {
            throw new RuntimeException("User is already enrolled in this course");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setId(enrollmentId);
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setProgress(0.0f);
        
        return enrollmentRepository.save(enrollment);
    }
    
    public void unenrollFromCourse(Long userId, Long courseId) {
        EnrollmentId enrollmentId = new EnrollmentId(userId, courseId);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        // Delete all progress for this user in this course
        List<Progress> progressList = progressRepository.findByUserIdAndCourseId(userId, courseId);
        progressRepository.deleteAll(progressList);
        
        enrollmentRepository.delete(enrollment);
    }
    
    public List<EnrollmentResponseDto> getUserEnrollments(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        
        return enrollments.stream().map(enrollment -> {
            Course course = courseRepository.findById(enrollment.getCourseId())
                .orElse(null);
            if (course == null) return null;
            
            User instructor = userRepository.findById(course.getInstructorId())
                .orElse(null);
            
            return new EnrollmentResponseDto(
                course.getId(),
                userId,
                course.getTitle(),
                course.getDescription(),
                instructor != null ? instructor.getName() : "Unknown",
                enrollment.getEnrolledAt(),
                enrollment.getProgress().doubleValue()
            );
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }
    
    public CourseProgressDto getCourseProgress(Long userId, Long courseId) {
        // Check if user is enrolled
        EnrollmentId enrollmentId = new EnrollmentId(userId, courseId);
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("User is not enrolled in this course"));
        
        // Get course modules
        List<Module> modules = moduleRepository.findByCourseIdOrderByModuleOrder(courseId);
        
        List<CourseProgressDto.ModuleProgressDto> moduleProgressList = new ArrayList<>();
        int totalCompletedLessons = 0;
        int totalLessons = 0;
        
        for (Module module : modules) {
            List<Lesson> lessons = lessonRepository.findByModuleIdOrderByLessonOrder(module.getId());
            int moduleCompletedLessons = 0;
            int moduleTotalLessons = lessons.size();
            
            for (Lesson lesson : lessons) {
                Optional<Progress> progress = progressRepository.findByUserIdAndLessonId(userId, lesson.getId());
                if (progress.isPresent() && progress.get().getCompleted()) {
                    moduleCompletedLessons++;
                }
            }
            
            totalCompletedLessons += moduleCompletedLessons;
            totalLessons += moduleTotalLessons;
            
            double moduleProgress = moduleTotalLessons > 0 ? 
                (double) moduleCompletedLessons / moduleTotalLessons * 100 : 0.0;
            
            moduleProgressList.add(new CourseProgressDto.ModuleProgressDto(
                module.getId(),
                module.getTitle(),
                moduleProgress,
                moduleCompletedLessons,
                moduleTotalLessons
            ));
        }
        
        double overallProgress = totalLessons > 0 ? 
            (double) totalCompletedLessons / totalLessons * 100 : 0.0;
        
        // Update enrollment progress
        enrollment.setProgress((float) overallProgress);
        enrollmentRepository.save(enrollment);
        
        return new CourseProgressDto(
            courseId,
            userId,
            overallProgress,
            totalCompletedLessons,
            totalLessons,
            moduleProgressList
        );
    }
    
    public Progress markLessonComplete(Long userId, Long lessonId) {
        // Check if lesson exists
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        // Check if user is enrolled in the course
        Long courseId = lesson.getModule().getCourse().getId();
        EnrollmentId enrollmentId = new EnrollmentId(userId, courseId);
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new RuntimeException("User is not enrolled in this course");
        }
        
        // Find or create progress record
        Optional<Progress> existingProgress = progressRepository.findByUserIdAndLessonId(userId, lessonId);
        Progress progress;
        
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = new Progress(userId, lessonId);
        }
        
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        
        return progressRepository.save(progress);
    }
    
    public StudentDashboardDto getStudentDashboard(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        
        int enrolledCourses = enrollments.size();
        int completedCourses = (int) enrollments.stream()
            .filter(enrollment -> enrollment.getProgress() >= 100.0f)
            .count();
        
        // Calculate total learning hours (simplified - based on number of completed lessons)
        double totalLearningHours = enrollments.stream()
            .mapToDouble(enrollment -> {
                // Estimate 1 hour per 10% progress
                return enrollment.getProgress() / 10.0;
            })
            .sum();
        
        // Get recent enrollments (last 5)
        List<StudentDashboardDto.RecentEnrollmentDto> recentEnrollments = enrollments.stream()
            .sorted((e1, e2) -> e2.getEnrolledAt().compareTo(e1.getEnrolledAt()))
            .limit(5)
            .map(enrollment -> {
                Course course = courseRepository.findById(enrollment.getCourseId()).orElse(null);
                if (course == null) return null;
                
                StudentDashboardDto.CourseDto courseDto = new StudentDashboardDto.CourseDto(
                    course.getId(),
                    course.getTitle(),
                    enrollment.getProgress().doubleValue()
                );
                
                return new StudentDashboardDto.RecentEnrollmentDto(
                    courseDto,
                    enrollment.getEnrolledAt().toString()
                );
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
        
        // Simple achievements (could be expanded)
        List<String> achievements = new ArrayList<>();
        if (completedCourses > 0) {
            achievements.add("Course Completer");
        }
        if (enrolledCourses >= 5) {
            achievements.add("Learning Enthusiast");
        }
        
        return new StudentDashboardDto(
            enrolledCourses,
            completedCourses,
            totalLearningHours,
            recentEnrollments,
            achievements
        );
    }
    
    public boolean isUserEnrolledInCourse(Long userId, Long courseId) {
        EnrollmentId enrollmentId = new EnrollmentId(userId, courseId);
        return enrollmentRepository.existsById(enrollmentId);
    }
    
    public List<Module> getCourseModulesForStudent(Long userId, Long courseId) {
        // Check if user is enrolled
        if (!isUserEnrolledInCourse(userId, courseId)) {
            throw new RuntimeException("User is not enrolled in this course");
        }
        
        return moduleRepository.findByCourseIdOrderByModuleOrder(courseId);
    }
    
    public List<Lesson> getModuleLessonsForStudent(Long userId, Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new RuntimeException("Module not found"));
        
        // Check if user is enrolled in the course
        if (!isUserEnrolledInCourse(userId, module.getCourse().getId())) {
            throw new RuntimeException("User is not enrolled in this course");
        }
        
        return lessonRepository.findByModuleIdOrderByLessonOrder(moduleId);
    }
    
    public Lesson getLessonForStudent(Long userId, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        // Check if user is enrolled in the course
        Long courseId = lesson.getModule().getCourse().getId();
        if (!isUserEnrolledInCourse(userId, courseId)) {
            throw new RuntimeException("User is not enrolled in this course");
        }
        
        return lesson;
    }
}
