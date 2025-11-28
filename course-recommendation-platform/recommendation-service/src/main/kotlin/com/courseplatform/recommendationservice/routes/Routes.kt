package com.courseplatform.recommendationservice.routes

import com.courseplatform.recommendationservice.model.ErrorResponse
import com.courseplatform.recommendationservice.service.RecommendationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.recommendationRoutes(recommendationService: RecommendationService) {
    route("/api/recommendations") {
        
        // GET /api/recommendations/{userId} - Get recommendations for user
        get("/{userId}") {
            val userId = call.parameters["userId"]?.toLongOrNull()
            
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid user ID", 400))
                return@get
            }
            
            try {
                val recommendations = recommendationService.getRecommendations(userId)
                call.respond(HttpStatusCode.OK, recommendations)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("Error fetching recommendations: ${e.message}", 500)
                )
            }
        }
        
        // POST /api/recommendations/{userId}/recalculate - Force recalculation
        post("/{userId}/recalculate") {
            val userId = call.parameters["userId"]?.toLongOrNull()
            
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid user ID", 400))
                return@post
            }
            
            try {
                recommendationService.recalculateRecommendations(userId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Recommendations recalculated for user $userId"))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("Error recalculating recommendations: ${e.message}", 500)
                )
            }
        }
        
        // GET /api/recommendations/{userId}/preferences - Get user preferences
        get("/{userId}/preferences") {
            val userId = call.parameters["userId"]?.toLongOrNull()
            
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid user ID", 400))
                return@get
            }
            
            try {
                val preferences = recommendationService.getUserPreferences(userId)
                call.respond(HttpStatusCode.OK, preferences)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("Error fetching preferences: ${e.message}", 500)
                )
            }
        }
        
        // POST /api/recommendations/sync-courses - Sync courses from CourseService
        post("/sync-courses") {
            try {
                recommendationService.fetchAndCacheCoursesFromService()
                call.respond(HttpStatusCode.OK, mapOf("message" to "Courses synced successfully"))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("Error syncing courses: ${e.message}", 500)
                )
            }
        }
    }
    
    // Health check endpoint
    get("/health") {
        call.respond(HttpStatusCode.OK, mapOf("status" to "UP", "service" to "recommendation-service"))
    }
}
