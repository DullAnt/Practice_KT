package com.courseplatform.ratingservice.kafka;

import com.courseplatform.common.dto.RatingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    
    private final KafkaTemplate<String, RatingEvent> kafkaTemplate;
    
    @Value("${kafka.topic.ratings}")
    private String ratingsTopic;
    
    public void sendRatingEvent(RatingEvent event) {
        String key = event.getUserId() + "-" + event.getCourseId();
        
        CompletableFuture<SendResult<String, RatingEvent>> future = 
            kafkaTemplate.send(ratingsTopic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Rating event sent successfully: userId={}, courseId={}, partition={}, offset={}",
                        event.getUserId(),
                        event.getCourseId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send rating event: userId={}, courseId={}, error={}",
                        event.getUserId(),
                        event.getCourseId(),
                        ex.getMessage());
            }
        });
    }
}
