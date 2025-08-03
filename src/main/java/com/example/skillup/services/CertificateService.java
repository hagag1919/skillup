package com.example.skillup.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.skillup.models.Certificate;
import com.example.skillup.models.Course;
import com.example.skillup.models.Enrollment;
import com.example.skillup.models.User;
import com.example.skillup.repo.CertificateRepository;
import com.example.skillup.repo.CourseRepository;
import com.example.skillup.repo.EnrollmentRepository;
import com.example.skillup.repo.UserRepository;

@Service
@Transactional
public class CertificateService {
    
    @Autowired
    private CertificateRepository certificateRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    public Certificate generateCertificate(Long userId, Long courseId) {
        // Verify user exists and is a student
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        
        User user = userOpt.get();
        if (!"STUDENT".equals(user.getRole())) {
            throw new RuntimeException("Only students can receive certificates");
        }
        
        // Verify course exists
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        Course course = courseOpt.get();
        
        // Check if user completed the course
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (enrollmentOpt.isEmpty()) {
            throw new RuntimeException("User is not enrolled in this course");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        if (enrollment.getCompletionDate() == null || enrollment.getProgress() < 100.0f) {
            throw new RuntimeException("Course must be completed before generating certificate");
        }
        
        // Check if certificate already exists
        Optional<Certificate> existingCert = certificateRepository.findByUserIdAndCourseId(userId, courseId);
        if (existingCert.isPresent()) {
            return existingCert.get(); // Return existing certificate
        }
        
        // Generate new certificate
        Certificate certificate = new Certificate();
        certificate.setUserId(userId);
        certificate.setCourseId(courseId);
        certificate.setCertUid(UUID.randomUUID().toString());
        certificate.setIssuedAt(LocalDate.now());
        
        // Generate certificate URL (this would typically point to a PDF generation service)
        String certUrl = "/api/certificates/" + certificate.getCertUid() + "/download";
        certificate.setCertUrl(certUrl);
        
        return certificateRepository.save(certificate);
    }
    
    public List<Certificate> getUserCertificates(Long userId) {
        return certificateRepository.findByUserId(userId);
    }
    
    public List<Certificate> getCourseCertificates(Long courseId) {
        return certificateRepository.findByCourseId(courseId);
    }
    
    public Optional<Certificate> getCertificateById(Long certificateId) {
        return certificateRepository.findById(certificateId);
    }
    
    public Optional<Certificate> getCertificateByUid(String certUid) {
        return certificateRepository.findByCertUid(certUid);
    }
    
    public void deleteCertificate(Long certificateId) {
        certificateRepository.deleteById(certificateId);
    }
    
    public boolean verifyCertificate(String certUid) {
        return certificateRepository.findByCertUid(certUid).isPresent();
    }
    
    // This method would be called when a user completes a course
    public Certificate autoGenerateCertificate(Long userId, Long courseId) {
        try {
            return generateCertificate(userId, courseId);
        } catch (RuntimeException e) {
            // Log the error but don't throw - certificate generation is not critical
            System.err.println("Failed to auto-generate certificate: " + e.getMessage());
            return null;
        }
    }
}
