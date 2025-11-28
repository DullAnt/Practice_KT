package com.courseplatform.courseservice;

import com.courseplatform.common.dto.CourseDTO;
import com.courseplatform.courseservice.entity.Course;
import com.courseplatform.courseservice.repository.CourseRepository;
import com.courseplatform.courseservice.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseServiceApplicationTests {
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
    }
    
    @Test
    void contextLoads() {
        assertNotNull(courseService);
    }
    
    @Test
    void testCreateCourse() {
        CourseDTO courseDTO = CourseDTO.builder()
                .title("Java Programming")
                .description("Learn Java from scratch")
                .category("Programming")
                .instructor("John Doe")
                .build();
        
        CourseDTO created = courseService.createCourse(courseDTO);
        
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Java Programming", created.getTitle());
        assertEquals("Programming", created.getCategory());
    }
    
    @Test
    void testGetAllCourses() {
        Course course1 = Course.builder()
                .title("Java Programming")
                .description("Learn Java")
                .category("Programming")
                .instructor("John Doe")
                .build();
        
        Course course2 = Course.builder()
                .title("Python Programming")
                .description("Learn Python")
                .category("Programming")
                .instructor("Jane Doe")
                .build();
        
        courseRepository.save(course1);
        courseRepository.save(course2);
        
        List<CourseDTO> courses = courseService.getAllCourses();
        
        assertEquals(2, courses.size());
    }
    
    @Test
    void testGetCoursesByCategory() {
        Course course1 = Course.builder()
                .title("Java Programming")
                .description("Learn Java")
                .category("Programming")
                .instructor("John Doe")
                .build();
        
        Course course2 = Course.builder()
                .title("Data Science")
                .description("Learn Data Science")
                .category("Data")
                .instructor("Jane Doe")
                .build();
        
        courseRepository.save(course1);
        courseRepository.save(course2);
        
        List<CourseDTO> programmingCourses = courseService.getCoursesByCategory("Programming");
        
        assertEquals(1, programmingCourses.size());
        assertEquals("Java Programming", programmingCourses.get(0).getTitle());
    }
    
    @Test
    void testUpdateCourse() {
        Course course = Course.builder()
                .title("Java Programming")
                .description("Learn Java")
                .category("Programming")
                .instructor("John Doe")
                .build();
        course = courseRepository.save(course);
        
        CourseDTO updateDTO = CourseDTO.builder()
                .title("Advanced Java Programming")
                .build();
        
        CourseDTO updated = courseService.updateCourse(course.getId(), updateDTO);
        
        assertEquals("Advanced Java Programming", updated.getTitle());
    }
    
    @Test
    void testDeleteCourse() {
        Course course = Course.builder()
                .title("Java Programming")
                .description("Learn Java")
                .category("Programming")
                .instructor("John Doe")
                .build();
        course = courseRepository.save(course);
        
        Long courseId = course.getId();
        courseService.deleteCourse(courseId);
        
        assertFalse(courseRepository.existsById(courseId));
    }
    
    @Test
    void testSearchCourses() {
        Course course = Course.builder()
                .title("Java Programming Masterclass")
                .description("Complete guide to Java")
                .category("Programming")
                .instructor("John Doe")
                .build();
        courseRepository.save(course);
        
        List<CourseDTO> results = courseService.searchCourses("Java");
        
        assertEquals(1, results.size());
        assertTrue(results.get(0).getTitle().contains("Java"));
    }
}
