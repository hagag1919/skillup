package com.example.skillup.dto;

import java.util.List;

public class CourseDetailDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Long instructorId;
    private String instructorName;
    private Integer totalModules;
    private Integer totalLessons;
    private Integer totalDurationMinutes;
    private Integer enrolledStudents;
    private List<ModuleDetailDto> modules;
    
    // Constructors
    public CourseDetailDto() {}
    
    // Getters and Setters
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public Long getInstructorId() {
        return instructorId;
    }
    
    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }
    
    public String getInstructorName() {
        return instructorName;
    }
    
    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
    
    public Integer getTotalModules() {
        return totalModules;
    }
    
    public void setTotalModules(Integer totalModules) {
        this.totalModules = totalModules;
    }
    
    public Integer getTotalLessons() {
        return totalLessons;
    }
    
    public void setTotalLessons(Integer totalLessons) {
        this.totalLessons = totalLessons;
    }
    
    public Integer getTotalDurationMinutes() {
        return totalDurationMinutes;
    }
    
    public void setTotalDurationMinutes(Integer totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }
    
    public Integer getEnrolledStudents() {
        return enrolledStudents;
    }
    
    public void setEnrolledStudents(Integer enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }
    
    public List<ModuleDetailDto> getModules() {
        return modules;
    }
    
    public void setModules(List<ModuleDetailDto> modules) {
        this.modules = modules;
    }
}

class ModuleDetailDto {
    private Long id;
    private String title;
    private Integer moduleOrder;
    private List<LessonDetailDto> lessons;
    
    // Getters and Setters
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
    
    public Integer getModuleOrder() {
        return moduleOrder;
    }
    
    public void setModuleOrder(Integer moduleOrder) {
        this.moduleOrder = moduleOrder;
    }
    
    public List<LessonDetailDto> getLessons() {
        return lessons;
    }
    
    public void setLessons(List<LessonDetailDto> lessons) {
        this.lessons = lessons;
    }
}

class LessonDetailDto {
    private Long id;
    private String title;
    private String videoUrl;
    private Integer durationSeconds;
    private Integer lessonOrder;
    
    // Getters and Setters
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
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public Integer getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public Integer getLessonOrder() {
        return lessonOrder;
    }
    
    public void setLessonOrder(Integer lessonOrder) {
        this.lessonOrder = lessonOrder;
    }
}
