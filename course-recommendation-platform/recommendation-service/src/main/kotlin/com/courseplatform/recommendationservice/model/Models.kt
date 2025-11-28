package com.courseplatform.recommendationservice.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

// Database Tables using Exposed
object UserRatings : LongIdTable("user_ratings") {
    val userId = long("user_id")
    val courseId = long("course_id")
    val rating = integer("rating")
    val comment = text("comment").nullable()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    
    init {
        uniqueIndex(userId, courseId)
    }
}

object Recommendations : LongIdTable("recommendations") {
    val userId = long("user_id")
    val courseId = long("course_id")
    val score = double("score")
    val reason = text("reason")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}

object CourseCategories : LongIdTable("course_categories") {
    val courseId = long("course_id").uniqueIndex()
    val category = varchar("category", 255)
    val averageRating = double("average_rating").default(0.0)
    val totalRatings = integer("total_ratings").default(0)
}

// DTOs
@Serializable
data class RatingEventDTO(
    val id: Long? = null,
    val userId: Long,
    val courseId: Long,
    val rating: Int,
    val comment: String? = null,
    val timestamp: String? = null
)

@Serializable
data class RecommendationDTO(
    val userId: Long,
    val courseId: Long,
    val score: Double,
    val reason: String
)

@Serializable
data class RecommendationResponse(
    val userId: Long,
    val recommendations: List<RecommendationDTO>,
    val generatedAt: String
)

@Serializable
data class CourseDTO(
    val id: Long,
    val title: String,
    val description: String? = null,
    val category: String,
    val instructor: String,
    val averageRating: Double? = null,
    val totalRatings: Int? = null
)

@Serializable
data class UserPreference(
    val userId: Long,
    val preferredCategories: List<String>,
    val averageRating: Double,
    val totalRatings: Int
)

@Serializable
data class ErrorResponse(
    val message: String,
    val status: Int
)
