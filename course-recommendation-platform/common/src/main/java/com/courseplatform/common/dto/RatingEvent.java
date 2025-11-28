package com.courseplatform.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingEvent {
    private Long id;
    private Long userId;
    private Long courseId;
    private Integer rating;
    private String comment;
    private LocalDateTime timestamp;
}
