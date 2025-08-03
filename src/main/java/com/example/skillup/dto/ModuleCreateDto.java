package com.example.skillup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ModuleCreateDto {
    
    @NotBlank(message = "Module title is required")
    @Size(min = 3, max = 200, message = "Module title must be between 3 and 200 characters")
    private String title;
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    private Integer moduleOrder = 1;
    
    // Constructors
    public ModuleCreateDto() {}
    
    public ModuleCreateDto(String title, Long courseId, Integer moduleOrder) {
        this.title = title;
        this.courseId = courseId;
        this.moduleOrder = moduleOrder;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public Integer getModuleOrder() {
        return moduleOrder;
    }
    
    public void setModuleOrder(Integer moduleOrder) {
        this.moduleOrder = moduleOrder;
    }
}
