package com.courseplatform.recommendationservice.service

import com.courseplatform.recommendationservice.model.*
import com.courseplatform.recommendationservice.repository.CourseCategoryRepository
import com.courseplatform.recommendationservice.repository.RatingRepository
import com.courseplatform.recommendationservice.repository.RecommendationRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecommendationService(
    private val ratingRepository: RatingRepository,
    private val recommendationRepository: RecommendationRepository,
    private val courseCategoryRepository: CourseCategoryRepository,
    private val httpClient: HttpClient,
    private val courseServiceUrl: String
) {
    private val logger = LoggerFactory.getLogger(RecommendationService::class.java)
    
    fun processRatingEvent(event: RatingEventDTO) {
        try {
            // Save rating to local database
            ratingRepository.saveOrUpdateRating(event)
            logger.info("Rating saved: userId=${event.userId}, courseId=${event.courseId}")
            
            // Recalculate recommendations for the user
            recalculateRecommendations(event.userId)
        } catch (e: Exception) {
            logger.error("Error processing rating event: ${e.message}", e)
        }
    }
    
    fun recalculateRecommendations(userId: Long) {
        try {
            logger.info("Recalculating recommendations for user: $userId")
            
            // Get user's highly rated courses
            val highRatedCourses = ratingRepository.getHighRatedCoursesByUser(userId, 4)
            
            // Get all rated courses by user (to exclude from recommendations)
            val ratedCourses = ratingRepository.getAllRatedCoursesByUser(userId)
            
            // Get preferred categories based on high ratings
            val preferredCategories = highRatedCourses.mapNotNull { courseId ->
                courseCategoryRepository.getCategoryByCourseId(courseId)
            }.groupingBy { it }.eachCount()
            
            val recommendations = mutableListOf<RecommendationDTO>()
            
            // Strategy 1: Category-based recommendations
            preferredCategories.entries.sortedByDescending { it.value }.take(3).forEach { (category, count) ->
                val topCourses = courseCategoryRepository.getTopRatedCoursesByCategory(category, 5)
                topCourses.filter { it.first !in ratedCourses }.forEach { (courseId, rating) ->
                    val score = calculateScore(rating, count.toDouble())
                    recommendations.add(
                        RecommendationDTO(
                            userId = userId,
                            courseId = courseId,
                            score = score,
                            reason = "Based on your interest in $category courses"
                        )
                    )
                }
            }
            
            // Strategy 2: Collaborative filtering (users who rated similar courses)
            highRatedCourses.take(3).forEach { courseId ->
                val similarUsers = ratingRepository.getUsersWhoRatedCourse(courseId)
                    .filter { it != userId }
                
                similarUsers.take(5).forEach { similarUserId ->
                    val theirHighRated = ratingRepository.getHighRatedCoursesByUser(similarUserId, 4)
                    theirHighRated.filter { it !in ratedCourses && it !in recommendations.map { r -> r.courseId } }
                        .take(2).forEach { recommendedCourseId ->
                            recommendations.add(
                                RecommendationDTO(
                                    userId = userId,
                                    courseId = recommendedCourseId,
                                    score = 0.7,
                                    reason = "Users with similar interests also liked this course"
                                )
                            )
                        }
                }
            }
            
            // Remove duplicates and sort by score
            val uniqueRecommendations = recommendations
                .distinctBy { it.courseId }
                .sortedByDescending { it.score }
                .take(10)
            
            // Save recommendations
            if (uniqueRecommendations.isNotEmpty()) {
                recommendationRepository.saveRecommendations(userId, uniqueRecommendations)
                logger.info("Saved ${uniqueRecommendations.size} recommendations for user $userId")
            }
        } catch (e: Exception) {
            logger.error("Error recalculating recommendations: ${e.message}", e)
        }
    }
    
    suspend fun getRecommendations(userId: Long): RecommendationResponse {
        val recommendations = recommendationRepository.getRecommendationsForUser(userId)
        
        return RecommendationResponse(
            userId = userId,
            recommendations = recommendations,
            generatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
    
    suspend fun fetchAndCacheCoursesFromService() {
        try {
            withContext(Dispatchers.IO) {
                logger.info("Fetching courses from course service: $courseServiceUrl")
                val response = httpClient.get("$courseServiceUrl/api/courses")
                
                if (response.status == HttpStatusCode.OK) {
                    val courses: List<CourseDTO> = response.body()
                    courses.forEach { course ->
                        courseCategoryRepository.saveOrUpdateCourseCategory(
                            courseId = course.id,
                            category = course.category,
                            avgRating = course.averageRating ?: 0.0,
                            totalRatings = course.totalRatings ?: 0
                        )
                    }
                    logger.info("Cached ${courses.size} courses from course service")
                }
            }
        } catch (e: Exception) {
            logger.error("Error fetching courses: ${e.message}", e)
        }
    }
    
    fun getUserPreferences(userId: Long): UserPreference {
        val ratings = ratingRepository.getRatingsByUserId(userId)
        val preferredCategories = ratings.mapNotNull { rating ->
            courseCategoryRepository.getCategoryByCourseId(rating.courseId)
        }.distinct()
        
        return UserPreference(
            userId = userId,
            preferredCategories = preferredCategories,
            averageRating = ratingRepository.getAverageRatingByUser(userId),
            totalRatings = ratingRepository.getTotalRatingsByUser(userId)
        )
    }
    
    private fun calculateScore(rating: Double, categoryWeight: Double): Double {
        return (rating / 5.0) * 0.6 + (categoryWeight / 10.0) * 0.4
    }
}
