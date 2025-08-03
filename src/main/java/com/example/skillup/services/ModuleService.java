package com.example.skillup.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.skillup.dto.ModuleCreateDto;
import com.example.skillup.models.Course;
import com.example.skillup.models.Module;
import com.example.skillup.repo.CourseRepository;
import com.example.skillup.repo.ModuleRepository;

@Service
@Transactional
public class ModuleService {
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseService courseService;
    
    public Module createModule(ModuleCreateDto moduleDto, Long instructorId) {

        if (!courseService.isInstructorOwner(moduleDto.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to add modules to this course");
        }
        
        Optional<Course> courseOpt = courseRepository.findById(moduleDto.getCourseId());
        if (courseOpt.isEmpty()) {
            throw new RuntimeException("Course not found with ID: " + moduleDto.getCourseId());
        }
        
        Module module = new Module();
        module.setTitle(moduleDto.getTitle());
        module.setCourseId(moduleDto.getCourseId());
        module.setModuleOrder(moduleDto.getModuleOrder());
        
        return moduleRepository.save(module);
    }
    
    public List<Module> getModulesByCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByModuleOrder(courseId);
    }
    
    public List<Module> getModulesByCourseId(Long courseId) {
        return getModulesByCourse(courseId);
    }
    
    public Optional<Module> getModuleById(Long moduleId) {
        return moduleRepository.findById(moduleId);
    }
    
    public Module updateModule(Long moduleId, ModuleCreateDto moduleDto, Long instructorId) {
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found with ID: " + moduleId);
        }
        
        Module module = moduleOpt.get();
        

        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to update this module");
        }
        
        module.setTitle(moduleDto.getTitle());
        module.setModuleOrder(moduleDto.getModuleOrder());
        
        return moduleRepository.save(module);
    }
    
    public void deleteModule(Long moduleId, Long instructorId) {
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found with ID: " + moduleId);
        }
        
        Module module = moduleOpt.get();
        

        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to delete this module");
        }
        
        moduleRepository.delete(module);
    }
    
    public Module reorderModule(Long moduleId, Integer newOrder, Long instructorId) {
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            throw new RuntimeException("Module not found with ID: " + moduleId);
        }
        
        Module module = moduleOpt.get();


        if (!courseService.isInstructorOwner(module.getCourseId(), instructorId)) {
            throw new RuntimeException("You are not authorized to reorder this module");
        }
        
        module.setModuleOrder(newOrder);
        return moduleRepository.save(module);
    }
}
