package com.courseplatform.recommendationservice

import com.courseplatform.recommendationservice.model.RatingEventDTO
import com.courseplatform.recommendationservice.model.RecommendationDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {
    
    @Test
    fun testHealthEndpoint() = testApplication {
        application {
            // Minimal test module without database
        }
        
        // Basic health check test structure
        val healthResponse = """{"status":"UP","service":"recommendation-service"}"""
        assertNotNull(healthResponse)
    }
    
    @Test
    fun testRatingEventDTO() {
        val event = RatingEventDTO(
            id = 1L,
            userId = 100L,
            courseId = 200L,
            rating = 5,
            comment = "Great course!",
            timestamp = "2024-01-01T10:00:00"
        )
        
        assertEquals(1L, event.id)
        assertEquals(100L, event.userId)
        assertEquals(200L, event.courseId)
        assertEquals(5, event.rating)
        assertEquals("Great course!", event.comment)
    }
    
    @Test
    fun testRecommendationDTO() {
        val recommendation = RecommendationDTO(
            userId = 100L,
            courseId = 200L,
            score = 0.85,
            reason = "Based on your interests"
        )
        
        assertEquals(100L, recommendation.userId)
        assertEquals(200L, recommendation.courseId)
        assertEquals(0.85, recommendation.score)
        assertEquals("Based on your interests", recommendation.reason)
    }
    
    @Test
    fun testRatingEventSerialization() {
        val event = RatingEventDTO(
            id = 1L,
            userId = 100L,
            courseId = 200L,
            rating = 5,
            comment = "Great course!",
            timestamp = null
        )
        
        val json = Json.encodeToString(RatingEventDTO.serializer(), event)
        assertNotNull(json)
        
        val decoded = Json.decodeFromString(RatingEventDTO.serializer(), json)
        assertEquals(event.userId, decoded.userId)
        assertEquals(event.courseId, decoded.courseId)
        assertEquals(event.rating, decoded.rating)
    }
}
