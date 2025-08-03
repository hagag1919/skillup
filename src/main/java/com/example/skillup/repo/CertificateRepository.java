package com.example.skillup.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.skillup.models.Certificate;
import com.example.skillup.models.Course;
import com.example.skillup.models.User;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    
    List<Certificate> findByUserId(Long userId);
    
    List<Certificate> findByCourseId(Long courseId);
    
    Optional<Certificate> findByUserIdAndCourseId(Long userId, Long courseId);
    
    Optional<Certificate> findByCertUid(String certUid);
    
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    
    boolean existsByCertUid(String certUid);
    
    List<Certificate> findByUser(User user);
    
    List<Certificate> findByCourse(Course course);
    
    List<Certificate> findByIssuedAt(LocalDate issuedAt);
    
    List<Certificate> findByIssuedAtBetween(LocalDate startDate, LocalDate endDate);
    
    List<Certificate> findByIssuedAtAfter(LocalDate date);
    
    List<Certificate> findByIssuedAtBefore(LocalDate date);
    
    long countByUserId(Long userId);
    
    long countByCourseId(Long courseId);
    
    long countByIssuedAt(LocalDate issuedAt);
    
    @Query("SELECT c FROM Certificate c WHERE c.course.category = :category")
    List<Certificate> findByCourseCategory(@Param("category") String category);
    
    @Query("SELECT c FROM Certificate c WHERE c.course.instructor.id = :instructorId")
    List<Certificate> findByInstructorId(@Param("instructorId") Long instructorId);
    
   
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.issuedAt BETWEEN :startDate AND :endDate")
    long countCertificatesIssuedBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c.courseId, COUNT(c) as certificateCount FROM Certificate c GROUP BY c.courseId ORDER BY certificateCount DESC")
    List<Object[]> findMostCertifiedCourses();
    
    @Query("SELECT c.userId, COUNT(c) as certificateCount FROM Certificate c GROUP BY c.userId ORDER BY certificateCount DESC")
    List<Object[]> findUsersWithMostCertificates();
    
    @Query("SELECT EXTRACT(YEAR FROM c.issuedAt) as year, EXTRACT(MONTH FROM c.issuedAt) as month, COUNT(c) as count FROM Certificate c GROUP BY EXTRACT(YEAR FROM c.issuedAt), EXTRACT(MONTH FROM c.issuedAt) ORDER BY year DESC, month DESC")
    List<Object[]> getCertificateStatisticsByMonth();
}
