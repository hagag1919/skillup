package com.example.skillup.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.skillup.dto.LessonCreateDto;
import com.example.skillup.models.Lesson;
import com.example.skillup.models.Module;
import com.example.skillup.repo.LessonRepository;
import com.example.skillup.repo.ModuleRepository;

@Service
@Transactional
public class LessonService {
    
    @Autowired
    private LessonRepository lessonRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private CourseService courseService;
    
    public Lesson createLesson(LessonCreateDto lessonDto, Long instructorId) {

        Optional<Module> moduleOpt = moduleRepository.findById(lessonDto.getModuleId());
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found with ID: " + lessonDto.getModuleId());
        }
        
        Module module = moduleOpt.get();
        

        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to add lessons to this module");
        }
        
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonDto.getTitle());
        lesson.setModuleId(lessonDto.getModuleId());
        lesson.setVideoUrl(lessonDto.getVideoUrl());
        lesson.setDurationSeconds(lessonDto.getDurationSeconds());
        lesson.setLessonOrder(lessonDto.getLessonOrder());
        
        return lessonRepository.save(lesson);
    }
    
    public Lesson createLesson(LessonCreateDto lessonDto, Long moduleId, Long instructorId) {
        // Set the moduleId in the DTO if not already set
        if (lessonDto.getModuleId() == null) {
            lessonDto.setModuleId(moduleId);
        }
        return createLesson(lessonDto, instructorId);
    }
    
    public List<Lesson> getLessonsByModule(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByLessonOrder(moduleId);
    }
    
    public List<Lesson> getLessonsByModuleId(Long moduleId, Long instructorId) {
        // Verify instructor ownership through module
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found with ID: " + moduleId);
        }
        
        Module module = moduleOpt.get();
        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to view lessons from this module");
        }
        
        return getLessonsByModule(moduleId);
    }
    
    public Optional<Lesson> getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId);
    }
    
    public Lesson updateLesson(Long lessonId, LessonCreateDto lessonDto, Long instructorId) {
        Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isEmpty()) {
            throw new RuntimeException("Lesson not found with ID: " + lessonId);
        }
        
        Lesson lesson = lessonOpt.get();
        

        Optional<Module> moduleOpt = moduleRepository.findById(lesson.getModuleId());
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found for this lesson");
        }
        
        Module module = moduleOpt.get();
        

        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to update this lesson");
        }
        
        lesson.setTitle(lessonDto.getTitle());
        lesson.setVideoUrl(lessonDto.getVideoUrl());
        lesson.setDurationSeconds(lessonDto.getDurationSeconds());
        lesson.setLessonOrder(lessonDto.getLessonOrder());
        
        return lessonRepository.save(lesson);
    }
    
    public void deleteLesson(Long lessonId, Long instructorId) {
        Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isEmpty()) {
            throw new RuntimeException("Lesson not found with ID: " + lessonId);
        }
        
        Lesson lesson = lessonOpt.get();
        

        Optional<Module> moduleOpt = moduleRepository.findById(lesson.getModuleId());
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found for this lesson");
        }
        
        Module module = moduleOpt.get();
        

        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to delete this lesson");
        }
        
        lessonRepository.delete(lesson);
    }
    
    public Lesson reorderLesson(Long lessonId, Integer newOrder, Long instructorId) {
        Optional<Lesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isEmpty()) {
            throw new RuntimeException("Lesson not found with ID: " + lessonId);
        }
        
        Lesson lesson = lessonOpt.get();
        

        Optional<Module> moduleOpt = moduleRepository.findById(lesson.getModuleId());
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found for this lesson");
        }
        
        Module module = moduleOpt.get();
        

        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to reorder this lesson");
        }
        
        lesson.setLessonOrder(newOrder);
        return lessonRepository.save(lesson);
    }
    
    public Integer getTotalCourseDuration(Long courseId) {
        return lessonRepository.getTotalDurationByCourseId(courseId);
    }
    
    public List<Lesson> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseId(courseId);
    }
}
