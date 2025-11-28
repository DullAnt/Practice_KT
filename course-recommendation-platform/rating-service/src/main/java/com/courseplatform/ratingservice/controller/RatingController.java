package com.courseplatform.ratingservice.controller;

import com.courseplatform.common.dto.RatingEvent;
import com.courseplatform.ratingservice.entity.Rating;
import com.courseplatform.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Slf4j
public class RatingController {
    
    private final RatingService ratingService;
    
    @PostMapping
    public ResponseEntity<RatingEvent> createRating(@RequestBody RatingRequest request) {
        log.info("Creating rating: userId={}, courseId={}, rating={}", 
                request.getUserId(), request.getCourseId(), request.getRating());
        
        RatingEvent event = ratingService.createRating(
                request.getUserId(),
                request.getCourseId(),
                request.getRating(),
                request.getComment()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
    
    @PutMapping
    public ResponseEntity<RatingEvent> updateRating(@RequestBody RatingRequest request) {
        log.info("Updating rating: userId={}, courseId={}, rating={}", 
                request.getUserId(), request.getCourseId(), request.getRating());
        
        RatingEvent event = ratingService.updateRating(
                request.getUserId(),
                request.getCourseId(),
                request.getRating(),
                request.getComment()
        );
        
        return ResponseEntity.ok(event);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsByUserId(userId));
    }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Rating>> getRatingsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(ratingService.getRatingsByCourseId(courseId));
    }
    
    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<Rating> getRatingByUserAndCourse(
            @PathVariable Long userId,
            @PathVariable Long courseId) {
        return ResponseEntity.ok(ratingService.getRatingByUserAndCourse(userId, courseId));
    }
    
    @DeleteMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long userId,
            @PathVariable Long courseId) {
        log.info("Deleting rating: userId={}, courseId={}", userId, courseId);
        ratingService.deleteRating(userId, courseId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/course/{courseId}/average")
    public ResponseEntity<Map<String, Object>> getCourseRatingStats(@PathVariable Long courseId) {
        Double avgRating = ratingService.getAverageRating(courseId);
        Integer totalRatings = ratingService.getTotalRatings(courseId);
        
        return ResponseEntity.ok(Map.of(
                "courseId", courseId,
                "averageRating", avgRating,
                "totalRatings", totalRatings
        ));
    }
}

record RatingRequest(Long userId, Long courseId, Integer rating, String comment) {
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
}
