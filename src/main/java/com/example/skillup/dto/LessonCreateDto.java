package com.example.skillup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LessonCreateDto {
    
    @NotBlank(message = "Lesson title is required")
    @Size(min = 3, max = 200, message = "Lesson title must be between 3 and 200 characters")
    private String title;
    
    @NotNull(message = "Module ID is required")
    private Long moduleId;
    
    private String videoUrl;
    
    private Integer durationSeconds;
    
    private Integer lessonOrder = 1;
    
    // Constructors
    public LessonCreateDto() {}
    
    public LessonCreateDto(String title, Long moduleId, String videoUrl, Integer durationSeconds, Integer lessonOrder) {
        this.title = title;
        this.moduleId = moduleId;
        this.videoUrl = videoUrl;
        this.durationSeconds = durationSeconds;
        this.lessonOrder = lessonOrder;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Long getModuleId() {
        return moduleId;
    }
    
    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
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
