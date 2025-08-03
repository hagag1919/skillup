package com.example.skillup.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "certificate", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "course_id"}),
    @UniqueConstraint(columnNames = {"cert_uid"})
})
public class Certificate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "cert_url", columnDefinition = "TEXT")
    private String certUrl;
    
    @Column(name = "issued_at")
    private LocalDate issuedAt;
    
    @Column(name = "cert_uid", unique = true, nullable = false)
    private String certUid;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference("user-certificates")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonBackReference("course-certificates")
    private Course course;
    
    // Constructors
    public Certificate() {}
    
    public Certificate(Long userId, Long courseId, String certUrl, String certUid) {
        this.userId = userId;
        this.courseId = courseId;
        this.certUrl = certUrl;
        this.certUid = certUid;
    }
    
    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.issuedAt == null) {
            this.issuedAt = LocalDate.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getCertUrl() {
        return certUrl;
    }
    
    public void setCertUrl(String certUrl) {
        this.certUrl = certUrl;
    }
    
    public LocalDate getIssuedAt() {
        return issuedAt;
    }
    
    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
    }
    
    public String getCertUid() {
        return certUid;
    }
    
    public void setCertUid(String certUid) {
        this.certUid = certUid;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
        return "Certificate{" +
                "id=" + id +
                ", userId=" + userId +
                ", courseId=" + courseId +
                ", certUid='" + certUid + '\'' +
                ", issuedAt=" + issuedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
