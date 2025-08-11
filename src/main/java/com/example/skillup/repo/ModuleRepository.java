package com.example.skillup.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.skillup.models.Course;
import com.example.skillup.models.Module;


@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    List<Module> findByCourse(Course course);
    
    List<Module> findByCourseId(Long courseId);
    
    List<Module> findByCourseIdOrderByModuleOrder(Long courseId);
    
    List<Module> findByTitleContainingIgnoreCase(String title);
    
    long countByCourse(Course course);
    
    long countByCourseId(Long courseId);
    
    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId AND m.moduleOrder = :order")
    Module findByCourseIdAndModuleOrder(@Param("courseId") Long courseId, @Param("order") Integer order);
    
    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.courseId = :courseId ORDER BY m.moduleOrder")
    List<Module> findByCourseIdWithLessons(@Param("courseId") Long courseId);
    
    @Query("SELECT MAX(m.moduleOrder) FROM Module m WHERE m.course.id = :courseId")
    Integer findMaxModuleOrderByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT DISTINCT m FROM Module m JOIN m.lessons l")
    List<Module> findModulesWithLessons();
    
    @Query("SELECT m FROM Module m WHERE m.course.category = :category")
    List<Module> findByCourseCategory(@Param("category") String category);
}
