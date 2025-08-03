package com.example.skillup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseCreateDto {
    
    @NotBlank(message = "Course title is required")
    @Size(min = 3, max = 200, message = "Course title must be between 3 and 200 characters")
    private String title;
    
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;
    
    @NotBlank(message = "Category is required")
    @Size(min = 2, max = 100, message = "Category must be between 2 and 100 characters")
    private String category;
    
    private String thumbnailUrl;
    
    // Constructors
    public CourseCreateDto() {}
    
    public CourseCreateDto(String title, String description, String category, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
    }
    
    // Getters and Setters
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
}
