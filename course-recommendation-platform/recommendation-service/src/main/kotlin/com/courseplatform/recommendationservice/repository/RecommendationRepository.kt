package com.courseplatform.recommendationservice.repository

import com.courseplatform.recommendationservice.model.RecommendationDTO
import com.courseplatform.recommendationservice.model.Recommendations
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class RecommendationRepository {

    fun saveRecommendations(userId: Long, recommendations: List<RecommendationDTO>) = transaction {
        Recommendations.deleteWhere { Recommendations.userId eq userId }

        recommendations.forEach { rec ->
            Recommendations.insert {
                it[Recommendations.userId] = rec.userId
                it[courseId] = rec.courseId
                it[score] = rec.score
                it[reason] = rec.reason
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun getRecommendationsForUser(userId: Long, limit: Int = 10): List<RecommendationDTO> = transaction {
        Recommendations.select { Recommendations.userId eq userId }
            .orderBy(Recommendations.score, SortOrder.DESC)
            .limit(limit)
            .map { row: ResultRow ->
                RecommendationDTO(
                    userId = row[Recommendations.userId],
                    courseId = row[Recommendations.courseId],
                    score = row[Recommendations.score],
                    reason = row[Recommendations.reason]
                )
            }
    }

    fun deleteRecommendationsForUser(userId: Long) = transaction {
        Recommendations.deleteWhere { Recommendations.userId eq userId }
    }
}