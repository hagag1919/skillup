package com.example.skillup.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.skillup.models.Course;
import com.example.skillup.models.User;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTitleContainingIgnoreCase(String title);

    List<Course> findByCategory(String category);
    
    List<Course> findByCategoryIgnoreCase(String category);

    List<Course> findByCategoryContainingIgnoreCase(String category);

    List<Course> findByInstructor(User instructor);

    List<Course> findByInstructorId(Long instructorId);
    
    Page<Course> findByInstructorId(Long instructorId, Pageable pageable);
    
    long countByActive(Boolean active);
    
    long countByFeatured(Boolean featured);
    
    List<Course> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    @Query("SELECT DISTINCT c.category FROM Course c ORDER BY c.category")
    List<String> findAllCategories();
    
    @Query("SELECT DISTINCT c.category FROM Course c ORDER BY c.category")
    List<String> findDistinctCategories();
    
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :courseId")
    Optional<Course> findByIdWithModules(@Param("courseId") Long courseId);
    
    long countByCategory(String category);

    long countByInstructor(User instructor);

    @Query("SELECT DISTINCT c FROM Course c JOIN c.enrollments e")
    List<Course> findCoursesWithEnrollments();

    @Query("SELECT c FROM Course c WHERE SIZE(c.enrollments) >= :minEnrollments ORDER BY SIZE(c.enrollments) DESC")
    List<Course> findPopularCourses(@Param("minEnrollments") int minEnrollments);

    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchCourses(@Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM Course c WHERE LOWER(c.instructor.name) LIKE LOWER(CONCAT('%', :instructorName, '%'))")
    List<Course> findByInstructorNameContainingIgnoreCase(@Param("instructorName") String instructorName);
    
    List<Course> findTop5ByOrderByCreatedAtDesc();
}
