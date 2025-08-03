package com.example.skillup.repo;

import com.example.skillup.models.Quiz;
import com.example.skillup.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    Optional<Quiz> findByLesson(Lesson lesson);
    
    Optional<Quiz> findByLessonId(Long lessonId);
    
    List<Quiz> findByPassingScore(Integer passingScore);
    
    List<Quiz> findByPassingScoreBetween(Integer minScore, Integer maxScore);
    
    List<Quiz> findByPassingScoreGreaterThanEqual(Integer minScore);
    
    List<Quiz> findByPassingScoreLessThanEqual(Integer maxScore);
    
    boolean existsByLessonId(Long lessonId);
    
    @Query("SELECT q FROM Quiz q WHERE q.lesson.module.course.id = :courseId")
    List<Quiz> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT q FROM Quiz q WHERE q.lesson.module.id = :moduleId")
    List<Quiz> findByModuleId(@Param("moduleId") Long moduleId);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.lesson.module.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.lesson.module.id = :moduleId")
    long countByModuleId(@Param("moduleId") Long moduleId);
    
    @Query("SELECT q FROM Quiz q WHERE q.passingScore > (SELECT AVG(q2.passingScore) FROM Quiz q2)")
    List<Quiz> findQuizzesWithHighPassingScores();
    
    @Query("SELECT AVG(q.passingScore) FROM Quiz q")
    Double getAveragePassingScore();
}
