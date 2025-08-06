package com.example.skillup.dto;

import java.util.List;

public class CourseProgressDto {
    private Long courseId;
    private Long userId;
    private Double overallProgress;
    private Integer completedLessons;
    private Integer totalLessons;
    private List<ModuleProgressDto> moduleProgress;
    
    public CourseProgressDto() {}
    
    public CourseProgressDto(Long courseId, Long userId, Double overallProgress, 
                           Integer completedLessons, Integer totalLessons, 
                           List<ModuleProgressDto> moduleProgress) {
        this.courseId = courseId;
        this.userId = userId;
        this.overallProgress = overallProgress;
        this.completedLessons = completedLessons;
        this.totalLessons = totalLessons;
        this.moduleProgress = moduleProgress;
    }
    
    // Getters and setters
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Double getOverallProgress() {
        return overallProgress;
    }
    
    public void setOverallProgress(Double overallProgress) {
        this.overallProgress = overallProgress;
    }
    
    public Integer getCompletedLessons() {
        return completedLessons;
    }
    
    public void setCompletedLessons(Integer completedLessons) {
        this.completedLessons = completedLessons;
    }
    
    public Integer getTotalLessons() {
        return totalLessons;
    }
    
    public void setTotalLessons(Integer totalLessons) {
        this.totalLessons = totalLessons;
    }
    
    public List<ModuleProgressDto> getModuleProgress() {
        return moduleProgress;
    }
    
    public void setModuleProgress(List<ModuleProgressDto> moduleProgress) {
        this.moduleProgress = moduleProgress;
    }
    
    public static class ModuleProgressDto {
        private Long moduleId;
        private String moduleName;
        private Double progress;
        private Integer completedLessons;
        private Integer totalLessons;
        
        public ModuleProgressDto() {}
        
        public ModuleProgressDto(Long moduleId, String moduleName, Double progress, 
                               Integer completedLessons, Integer totalLessons) {
            this.moduleId = moduleId;
            this.moduleName = moduleName;
            this.progress = progress;
            this.completedLessons = completedLessons;
            this.totalLessons = totalLessons;
        }
        
        // Getters and setters
        public Long getModuleId() {
            return moduleId;
        }
        
        public void setModuleId(Long moduleId) {
            this.moduleId = moduleId;
        }
        
        public String getModuleName() {
            return moduleName;
        }
        
        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }
        
        public Double getProgress() {
            return progress;
        }
        
        public void setProgress(Double progress) {
            this.progress = progress;
        }
        
        public Integer getCompletedLessons() {
            return completedLessons;
        }
        
        public void setCompletedLessons(Integer completedLessons) {
            this.completedLessons = completedLessons;
        }
        
        public Integer getTotalLessons() {
            return totalLessons;
        }
        
        public void setTotalLessons(Integer totalLessons) {
            this.totalLessons = totalLessons;
        }
    }
}
