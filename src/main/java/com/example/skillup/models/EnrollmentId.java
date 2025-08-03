package com.example.skillup.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EnrollmentId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "course_id")
    private Long courseId;
    
    // Constructors
    public EnrollmentId() {}
    
    public EnrollmentId(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(courseId, that.courseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, courseId);
    }
    
    @Override
    public String toString() {
        return "EnrollmentId{" +
                "userId=" + userId +
                ", courseId=" + courseId +
                '}';
    }
}
