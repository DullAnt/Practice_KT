package com.courseplatform.ratingservice.service;

import com.courseplatform.common.dto.RatingEvent;
import com.courseplatform.ratingservice.entity.Rating;
import com.courseplatform.ratingservice.kafka.KafkaProducerService;
import com.courseplatform.ratingservice.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    
    private final RatingRepository ratingRepository;
    private final KafkaProducerService kafkaProducerService;
    private final RestTemplate restTemplate;
    
    @Value("${services.course-service.url}")
    private String courseServiceUrl;
    
    @Transactional
    public RatingEvent createRating(Long userId, Long courseId, Integer rating, String comment) {
        // Check if rating already exists
        if (ratingRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("User has already rated this course");
        }
        
        // Validate rating value
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        Rating ratingEntity = Rating.builder()
                .userId(userId)
                .courseId(courseId)
                .rating(rating)
                .comment(comment)
                .build();
        
        Rating savedRating = ratingRepository.save(ratingEntity);
        log.info("Rating created: userId={}, courseId={}, rating={}", userId, courseId, rating);
        
        // Create and send rating event to Kafka
        RatingEvent event = RatingEvent.builder()
                .id(savedRating.getId())
                .userId(userId)
                .courseId(courseId)
                .rating(rating)
                .comment(comment)
                .timestamp(LocalDateTime.now())
                .build();
        
        kafkaProducerService.sendRatingEvent(event);
        
        // Update course average rating
        updateCourseRating(courseId);
        
        return event;
    }
    
    @Transactional
    public RatingEvent updateRating(Long userId, Long courseId, Integer rating, String comment) {
        Rating ratingEntity = ratingRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        
        // Validate rating value
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        ratingEntity.setRating(rating);
        ratingEntity.setComment(comment);
        
        Rating updatedRating = ratingRepository.save(ratingEntity);
        log.info("Rating updated: userId={}, courseId={}, rating={}", userId, courseId, rating);
        
        // Create and send rating event to Kafka
        RatingEvent event = RatingEvent.builder()
                .id(updatedRating.getId())
                .userId(userId)
                .courseId(courseId)
                .rating(rating)
                .comment(comment)
                .timestamp(LocalDateTime.now())
                .build();
        
        kafkaProducerService.sendRatingEvent(event);
        
        // Update course average rating
        updateCourseRating(courseId);
        
        return event;
    }
    
    public List<Rating> getRatingsByUserId(Long userId) {
        return ratingRepository.findByUserId(userId);
    }
    
    public List<Rating> getRatingsByCourseId(Long courseId) {
        return ratingRepository.findByCourseId(courseId);
    }
    
    public Rating getRatingByUserAndCourse(Long userId, Long courseId) {
        return ratingRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
    }
    
    @Transactional
    public void deleteRating(Long userId, Long courseId) {
        Rating rating = ratingRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        
        ratingRepository.delete(rating);
        log.info("Rating deleted: userId={}, courseId={}", userId, courseId);
        
        // Update course average rating
        updateCourseRating(courseId);
    }
    
    public Double getAverageRating(Long courseId) {
        Double avg = ratingRepository.getAverageRatingByCourseId(courseId);
        return avg != null ? avg : 0.0;
    }
    
    public Integer getTotalRatings(Long courseId) {
        Integer total = ratingRepository.getTotalRatingsByCourseId(courseId);
        return total != null ? total : 0;
    }
    
    private void updateCourseRating(Long courseId) {
        try {
            Double avgRating = getAverageRating(courseId);
            Integer totalRatings = getTotalRatings(courseId);
            
            String url = String.format("%s/api/courses/%d/rating?averageRating=%f&totalRatings=%d",
                    courseServiceUrl, courseId, avgRating, totalRatings);
            
            restTemplate.put(url, null);
            log.info("Course rating updated: courseId={}, avgRating={}, totalRatings={}", 
                    courseId, avgRating, totalRatings);
        } catch (Exception e) {
            log.error("Failed to update course rating: {}", e.getMessage());
        }
    }
}
