package com.courseplatform.recommendationservice.repository

import com.courseplatform.recommendationservice.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class RatingRepository {

    fun saveOrUpdateRating(event: RatingEventDTO) = transaction {
        val existing = UserRatings.select {
            (UserRatings.userId eq event.userId) and (UserRatings.courseId eq event.courseId)
        }.singleOrNull()

        if (existing != null) {
            UserRatings.update({
                (UserRatings.userId eq event.userId) and (UserRatings.courseId eq event.courseId)
            }) {
                it[rating] = event.rating
                it[comment] = event.comment
                it[updatedAt] = LocalDateTime.now()
            }
        } else {
            UserRatings.insert {
                it[userId] = event.userId
                it[courseId] = event.courseId
                it[rating] = event.rating
                it[comment] = event.comment
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun getRatingsByUserId(userId: Long): List<RatingEventDTO> = transaction {
        UserRatings.select { UserRatings.userId eq userId }
            .map { row: ResultRow ->
                RatingEventDTO(
                    id = row[UserRatings.id].value,
                    userId = row[UserRatings.userId],
                    courseId = row[UserRatings.courseId],
                    rating = row[UserRatings.rating],
                    comment = row[UserRatings.comment],
                    timestamp = row[UserRatings.createdAt].toString()
                )
            }
    }

    fun getHighRatedCoursesByUser(userId: Long, minRating: Int = 4): List<Long> = transaction {
        UserRatings.select {
            (UserRatings.userId eq userId) and (UserRatings.rating greaterEq minRating)
        }.map { row: ResultRow -> row[UserRatings.courseId] }
    }

    fun getUsersWhoRatedCourse(courseId: Long): List<Long> = transaction {
        UserRatings.select { UserRatings.courseId eq courseId }
            .map { row: ResultRow -> row[UserRatings.userId] }
    }

    fun getAverageRatingByUser(userId: Long): Double = transaction {
        UserRatings.slice(UserRatings.rating.avg())
            .select { UserRatings.userId eq userId }
            .singleOrNull()
            ?.get(UserRatings.rating.avg())
            ?.toDouble() ?: 0.0
    }

    fun getTotalRatingsByUser(userId: Long): Int = transaction {
        UserRatings.select { UserRatings.userId eq userId }.count().toInt()
    }

    fun getAllRatedCoursesByUser(userId: Long): List<Long> = transaction {
        UserRatings.select { UserRatings.userId eq userId }
            .map { row: ResultRow -> row[UserRatings.courseId] }
    }
}