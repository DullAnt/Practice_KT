package com.courseplatform.recommendationservice.config

import com.courseplatform.recommendationservice.model.CourseCategories
import com.courseplatform.recommendationservice.model.Recommendations
import com.courseplatform.recommendationservice.model.UserRatings
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseConfig {
    private val logger = LoggerFactory.getLogger(DatabaseConfig::class.java)
    
    fun init(environment: ApplicationEnvironment) {
        val dbUrl = environment.config.property("database.url").getString()
        val dbUser = environment.config.property("database.user").getString()
        val dbPassword = environment.config.property("database.password").getString()
        val dbDriver = environment.config.property("database.driver").getString()
        
        logger.info("Connecting to database: $dbUrl")
        
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = dbUrl
            driverClassName = dbDriver
            username = dbUser
            password = dbPassword
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 30000
            connectionTimeout = 30000
            maxLifetime = 1800000
        }
        
        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)
        
        transaction {
            SchemaUtils.create(UserRatings, Recommendations, CourseCategories)
            logger.info("Database tables created/verified")
        }
    }
}
