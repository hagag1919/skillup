package com.example.skillup.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.skillup.models.Course;
import com.example.skillup.models.Enrollment;
import com.example.skillup.models.EnrollmentId;
import com.example.skillup.models.User;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    
 
    List<Enrollment> findByUserId(Long userId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);
    
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    
    List<Enrollment> findByUser(User user);
    
    List<Enrollment> findByCourse(Course course);
    
    List<Enrollment> findByCompletionDateIsNotNull();
    
    List<Enrollment> findByCompletionDateIsNull();
    
    List<Enrollment> findByUserIdAndCompletionDateIsNotNull(Long userId);
    
    List<Enrollment> findByUserIdAndCompletionDateIsNull(Long userId);
    
    List<Enrollment> findByProgressBetween(Float minProgress, Float maxProgress);
    
    List<Enrollment> findByProgressGreaterThan(Float progress);
    
    List<Enrollment> findByCompletionDate(LocalDate completionDate);
    
    List<Enrollment> findByCompletionDateBetween(LocalDate startDate, LocalDate endDate);
    
    long countByCourseId(Long courseId);
    
    long countByUserId(Long userId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.completionDate IS NOT NULL")
    long countCompletedEnrollments();
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.completionDate IS NULL")
    long countInProgressEnrollments();
    
    @Query("SELECT AVG(e.progress) FROM Enrollment e WHERE e.courseId = :courseId")
    Double getAverageProgressByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT (COUNT(e) * 100.0 / (SELECT COUNT(e2) FROM Enrollment e2 WHERE e2.courseId = :courseId)) FROM Enrollment e WHERE e.courseId = :courseId AND e.completionDate IS NOT NULL")
    Double getCompletionRateByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT e.courseId, COUNT(e) as enrollmentCount FROM Enrollment e GROUP BY e.courseId ORDER BY enrollmentCount DESC")
    List<Object[]> findMostPopularCourses();
    
    @Query("SELECT e.userId, COUNT(e) as enrollmentCount FROM Enrollment e GROUP BY e.userId ORDER BY enrollmentCount DESC")
    List<Object[]> findUsersWithMostEnrollments();
}
