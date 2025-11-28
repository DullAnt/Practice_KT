package com.courseplatform.courseservice.repository;

import com.courseplatform.courseservice.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByCategory(String category);
    
    List<Course> findByInstructor(String instructor);
    
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchByKeyword(@Param("keyword") String keyword);
    
    List<Course> findByAverageRatingGreaterThanEqual(Double rating);
    
    @Query("SELECT c FROM Course c ORDER BY c.averageRating DESC")
    List<Course> findTopRatedCourses();
    
    @Query("SELECT DISTINCT c.category FROM Course c")
    List<String> findAllCategories();
}
