package com.example.skillup.repo;

import com.example.skillup.models.Lesson;
import com.example.skillup.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    List<Lesson> findByModule(Module module);
    
    List<Lesson> findByModuleId(Long moduleId);
    
    List<Lesson> findByModuleIdOrderByLessonOrder(Long moduleId);
    
    List<Lesson> findByTitleContainingIgnoreCase(String title);
    
    long countByModule(Module module);
    
    long countByModuleId(Long moduleId);
    
    @Query("SELECT l FROM Lesson l WHERE l.module.id = :moduleId AND l.lessonOrder = :order")
    Lesson findByModuleIdAndLessonOrder(@Param("moduleId") Long moduleId, @Param("order") Integer order);
    
    @Query("SELECT MAX(l.lessonOrder) FROM Lesson l WHERE l.module.id = :moduleId")
    Integer findMaxLessonOrderByModuleId(@Param("moduleId") Long moduleId);
    
    @Query("SELECT DISTINCT l FROM Lesson l JOIN l.quizzes q")
    List<Lesson> findLessonsWithQuizzes();
    
    @Query("SELECT l FROM Lesson l WHERE l.module.course.id = :courseId")
    List<Lesson> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT l FROM Lesson l WHERE l.module.course.id = :courseId ORDER BY l.module.moduleOrder, l.lessonOrder")
    List<Lesson> findByCourseIdOrderByModuleAndLessonOrder(@Param("courseId") Long courseId);
    
    @Query("SELECT SUM(l.durationSeconds) FROM Lesson l WHERE l.module.course.id = :courseId")
    Long calculateTotalDurationByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COALESCE(SUM(l.durationSeconds), 0) FROM Lesson l WHERE l.module.course.id = :courseId")
    Integer getTotalDurationByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT SUM(l.durationSeconds) FROM Lesson l WHERE l.module.id = :moduleId")
    Long calculateTotalDurationByModuleId(@Param("moduleId") Long moduleId);
}
