package com.courseplatform.recommendationservice.repository

import com.courseplatform.recommendationservice.model.CourseCategories
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CourseCategoryRepository {

    fun saveOrUpdateCourseCategory(courseId: Long, category: String, avgRating: Double = 0.0, totalRatings: Int = 0) = transaction {
        val existing = CourseCategories.select { CourseCategories.courseId eq courseId }
            .singleOrNull()

        if (existing != null) {
            CourseCategories.update({ CourseCategories.courseId eq courseId }) {
                it[CourseCategories.category] = category
                it[averageRating] = avgRating
                it[CourseCategories.totalRatings] = totalRatings
            }
        } else {
            CourseCategories.insert {
                it[CourseCategories.courseId] = courseId
                it[CourseCategories.category] = category
                it[averageRating] = avgRating
                it[CourseCategories.totalRatings] = totalRatings
            }
        }
    }

    fun getCategoryByCourseId(courseId: Long): String? = transaction {
        CourseCategories.select { CourseCategories.courseId eq courseId }
            .singleOrNull()
            ?.get(CourseCategories.category)
    }

    fun getCoursesByCategory(category: String): List<Long> = transaction {
        CourseCategories.select { CourseCategories.category eq category }
            .map { row: ResultRow -> row[CourseCategories.courseId] }
    }

    fun getTopRatedCoursesByCategory(category: String, limit: Int = 10): List<Pair<Long, Double>> = transaction {
        CourseCategories.select { CourseCategories.category eq category }
            .orderBy(CourseCategories.averageRating, SortOrder.DESC)
            .limit(limit)
            .map { row: ResultRow -> Pair(row[CourseCategories.courseId], row[CourseCategories.averageRating]) }
    }

    fun getAllCategories(): List<String> = transaction {
        CourseCategories.selectAll()
            .map { row: ResultRow -> row[CourseCategories.category] }
            .distinct()
    }
}