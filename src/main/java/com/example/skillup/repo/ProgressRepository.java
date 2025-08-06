package com.example.skillup.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.skillup.models.Progress;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    
    List<Progress> findByUserId(Long userId);
    
    List<Progress> findByLessonId(Long lessonId);
    
    Optional<Progress> findByUserIdAndLessonId(Long userId, Long lessonId);
    
    List<Progress> findByUserIdAndCompleted(Long userId, Boolean completed);
    
    @Query("SELECT p FROM Progress p WHERE p.userId = :userId AND p.lesson.module.course.id = :courseId")
    List<Progress> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.userId = :userId AND p.lesson.module.course.id = :courseId AND p.completed = true")
    Long countCompletedLessonsByCourseAndUser(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.userId = :userId AND p.lesson.module.course.id = :courseId")
    Long countTotalLessonsByCourseAndUser(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    @Query("SELECT p FROM Progress p WHERE p.userId = :userId AND p.lesson.module.id = :moduleId")
    List<Progress> findByUserIdAndModuleId(@Param("userId") Long userId, @Param("moduleId") Long moduleId);
}
