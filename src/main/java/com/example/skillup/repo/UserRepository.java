package com.example.skillup.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.skillup.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(String role);
    
    Page<User> findByRole(String role, Pageable pageable);
    
    List<User> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT u FROM User u WHERE u.role = 'INSTRUCTOR'")
    List<User> findAllInstructors();
    
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT'")
    List<User> findAllStudents();
    
    long countByRole(String role);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.coursesCreated c")
    List<User> findUsersWithCreatedCourses();
    
    @Query("SELECT u FROM User u JOIN u.enrollments e WHERE e.courseId = :courseId")
    List<User> findUsersEnrolledInCourse(@Param("courseId") Long courseId);

    List<User> findTop5ByOrderByCreatedAtDesc();
}
