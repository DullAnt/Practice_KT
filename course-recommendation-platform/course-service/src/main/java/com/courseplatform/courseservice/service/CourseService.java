package com.courseplatform.courseservice.service;

import com.courseplatform.common.dto.CourseDTO;
import com.courseplatform.courseservice.entity.Course;
import com.courseplatform.courseservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    
    private final CourseRepository courseRepository;
    
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
        return mapToDTO(course);
    }
    
    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .category(courseDTO.getCategory())
                .instructor(courseDTO.getInstructor())
                .averageRating(0.0)
                .totalRatings(0)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        log.info("Course created: {}", savedCourse.getTitle());
        return mapToDTO(savedCourse);
    }
    
    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
        
        if (courseDTO.getTitle() != null) {
            course.setTitle(courseDTO.getTitle());
        }
        if (courseDTO.getDescription() != null) {
            course.setDescription(courseDTO.getDescription());
        }
        if (courseDTO.getCategory() != null) {
            course.setCategory(courseDTO.getCategory());
        }
        if (courseDTO.getInstructor() != null) {
            course.setInstructor(courseDTO.getInstructor());
        }
        
        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated: {}", updatedCourse.getTitle());
        return mapToDTO(updatedCourse);
    }
    
    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
        log.info("Course deleted: {}", id);
    }
    
    public List<CourseDTO> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CourseDTO> searchCourses(String keyword) {
        return courseRepository.searchByKeyword(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CourseDTO> getTopRatedCourses() {
        return courseRepository.findTopRatedCourses().stream()
                .limit(10)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<String> getAllCategories() {
        return courseRepository.findAllCategories();
    }
    
    @Transactional
    public void updateCourseRating(Long courseId, Double newAverageRating, Integer totalRatings) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        course.setAverageRating(newAverageRating);
        course.setTotalRatings(totalRatings);
        courseRepository.save(course);
        log.info("Course rating updated: {} - Rating: {}", course.getTitle(), newAverageRating);
    }
    
    private CourseDTO mapToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .instructor(course.getInstructor())
                .averageRating(course.getAverageRating())
                .totalRatings(course.getTotalRatings())
                .build();
    }
}
