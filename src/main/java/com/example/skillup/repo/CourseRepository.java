package com.example.skillup.repo;

import com.example.skillup.models.Course;
import com.example.skillup.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTitleContainingIgnoreCase(String title);

    List<Course> findByCategory(String category);

    List<Course> findByCategoryContainingIgnoreCase(String category);

    List<Course> findByInstructor(User instructor);

    List<Course> findByInstructorId(Long instructorId);

    @Query("SELECT DISTINCT c.category FROM Course c ORDER BY c.category")
    List<String> findAllCategories();

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
}
