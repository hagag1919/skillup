package com.example.skillup.dto;

import java.util.List;

public class StudentDashboardDto {
    private Integer enrolledCourses;
    private Integer completedCourses;
    private Double totalLearningHours;
    private List<RecentEnrollmentDto> recentEnrollments;
    private List<String> achievements;
    
    public StudentDashboardDto() {}
    
    public StudentDashboardDto(Integer enrolledCourses, Integer completedCourses, 
                             Double totalLearningHours, List<RecentEnrollmentDto> recentEnrollments,
                             List<String> achievements) {
        this.enrolledCourses = enrolledCourses;
        this.completedCourses = completedCourses;
        this.totalLearningHours = totalLearningHours;
        this.recentEnrollments = recentEnrollments;
        this.achievements = achievements;
    }
    
    // Getters and setters
    public Integer getEnrolledCourses() {
        return enrolledCourses;
    }
    
    public void setEnrolledCourses(Integer enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
    
    public Integer getCompletedCourses() {
        return completedCourses;
    }
    
    public void setCompletedCourses(Integer completedCourses) {
        this.completedCourses = completedCourses;
    }
    
    public Double getTotalLearningHours() {
        return totalLearningHours;
    }
    
    public void setTotalLearningHours(Double totalLearningHours) {
        this.totalLearningHours = totalLearningHours;
    }
    
    public List<RecentEnrollmentDto> getRecentEnrollments() {
        return recentEnrollments;
    }
    
    public void setRecentEnrollments(List<RecentEnrollmentDto> recentEnrollments) {
        this.recentEnrollments = recentEnrollments;
    }
    
    public List<String> getAchievements() {
        return achievements;
    }
    
    public void setAchievements(List<String> achievements) {
        this.achievements = achievements;
    }
    
    public static class RecentEnrollmentDto {
        private CourseDto course;
        private String enrolledAt;
        
        public RecentEnrollmentDto() {}
        
        public RecentEnrollmentDto(CourseDto course, String enrolledAt) {
            this.course = course;
            this.enrolledAt = enrolledAt;
        }
        
        // Getters and setters
        public CourseDto getCourse() {
            return course;
        }
        
        public void setCourse(CourseDto course) {
            this.course = course;
        }
        
        public String getEnrolledAt() {
            return enrolledAt;
        }
        
        public void setEnrolledAt(String enrolledAt) {
            this.enrolledAt = enrolledAt;
        }
    }
    
    public static class CourseDto {
        private Long id;
        private String title;
        private Double progress;
        
        public CourseDto() {}
        
        public CourseDto(Long id, String title, Double progress) {
            this.id = id;
            this.title = title;
            this.progress = progress;
        }
        
        // Getters and setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public Double getProgress() {
            return progress;
        }
        
        public void setProgress(Double progress) {
            this.progress = progress;
        }
    }
}
