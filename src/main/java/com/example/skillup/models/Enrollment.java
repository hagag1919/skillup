package com.example.skillup.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "enrollment")
public class Enrollment {
    
    @EmbeddedId
    private EnrollmentId id;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
    
    @Column(name = "course_id", insertable = false, updatable = false)
    private Long courseId;
    
    @Column(columnDefinition = "FLOAT DEFAULT 0.0")
    private Float progress = 0.0f;
    
    @Column(name = "completion_date")
    private LocalDate completionDate;
    
    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference("user-enrollments")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonBackReference("course-enrollments")
    private Course course;
    
    // Constructors
    public Enrollment() {}
    
    public Enrollment(Long userId, Long courseId) {
        this.id = new EnrollmentId(userId, courseId);
        this.userId = userId;
        this.courseId = courseId;
    }
    
    public Enrollment(Long userId, Long courseId, Float progress) {
        this.id = new EnrollmentId(userId, courseId);
        this.userId = userId;
        this.courseId = courseId;
        this.progress = progress;
    }
    
    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.enrolledAt = LocalDateTime.now();
        if (this.progress == null) {
            this.progress = 0.0f;
        }
    }
    
    // Getters and Setters
    public EnrollmentId getId() {
        return id;
    }
    
    public void setId(EnrollmentId id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
        if (this.id == null) {
            this.id = new EnrollmentId();
        }
        this.id.setUserId(userId);
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
        if (this.id == null) {
            this.id = new EnrollmentId();
        }
        this.id.setCourseId(courseId);
    }
    
    public Float getProgress() {
        return progress;
    }
    
    public void setProgress(Float progress) {
        this.progress = progress;
    }
    
    public LocalDate getCompletionDate() {
        return completionDate;
    }
    
    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }
    
    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }
    
    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    @Override
    public String toString() {
        return "Enrollment{" +
                "userId=" + userId +
                ", courseId=" + courseId +
                ", progress=" + progress +
                ", completionDate=" + completionDate +
                ", enrolledAt=" + enrolledAt +
                '}';
    }
}
