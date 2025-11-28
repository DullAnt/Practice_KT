package com.courseplatform.ratingservice;

import com.courseplatform.ratingservice.entity.Rating;
import com.courseplatform.ratingservice.repository.RatingRepository;
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
class RatingServiceApplicationTests {
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
    }
    
    @Test
    void contextLoads() {
        assertNotNull(ratingRepository);
    }
    
    @Test
    void testSaveRating() {
        Rating rating = Rating.builder()
                .userId(1L)
                .courseId(1L)
                .rating(5)
                .comment("Excellent course!")
                .build();
        
        Rating saved = ratingRepository.save(rating);
        
        assertNotNull(saved.getId());
        assertEquals(5, saved.getRating());
        assertEquals("Excellent course!", saved.getComment());
    }
    
    @Test
    void testFindByUserId() {
        Rating rating1 = Rating.builder()
                .userId(1L)
                .courseId(1L)
                .rating(5)
                .build();
        
        Rating rating2 = Rating.builder()
                .userId(1L)
                .courseId(2L)
                .rating(4)
                .build();
        
        ratingRepository.save(rating1);
        ratingRepository.save(rating2);
        
        List<Rating> ratings = ratingRepository.findByUserId(1L);
        
        assertEquals(2, ratings.size());
    }
    
    @Test
    void testFindByCourseId() {
        Rating rating1 = Rating.builder()
                .userId(1L)
                .courseId(1L)
                .rating(5)
                .build();
        
        Rating rating2 = Rating.builder()
                .userId(2L)
                .courseId(1L)
                .rating(4)
                .build();
        
        ratingRepository.save(rating1);
        ratingRepository.save(rating2);
        
        List<Rating> ratings = ratingRepository.findByCourseId(1L);
        
        assertEquals(2, ratings.size());
    }
    
    @Test
    void testGetAverageRating() {
        Rating rating1 = Rating.builder()
                .userId(1L)
                .courseId(1L)
                .rating(5)
                .build();
        
        Rating rating2 = Rating.builder()
                .userId(2L)
                .courseId(1L)
                .rating(3)
                .build();
        
        ratingRepository.save(rating1);
        ratingRepository.save(rating2);
        
        Double avgRating = ratingRepository.getAverageRatingByCourseId(1L);
        
        assertNotNull(avgRating);
        assertEquals(4.0, avgRating);
    }
    
    @Test
    void testUniqueConstraint() {
        Rating rating1 = Rating.builder()
                .userId(1L)
                .courseId(1L)
                .rating(5)
                .build();
        
        ratingRepository.save(rating1);
        
        assertTrue(ratingRepository.existsByUserIdAndCourseId(1L, 1L));
        assertFalse(ratingRepository.existsByUserIdAndCourseId(1L, 2L));
    }
}
