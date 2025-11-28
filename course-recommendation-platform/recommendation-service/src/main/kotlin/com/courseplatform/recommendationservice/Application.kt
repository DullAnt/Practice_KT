package com.courseplatform.recommendationservice

import com.courseplatform.recommendationservice.config.DatabaseConfig
import com.courseplatform.recommendationservice.kafka.RatingEventConsumer
import com.courseplatform.recommendationservice.model.ErrorResponse
import com.courseplatform.recommendationservice.repository.CourseCategoryRepository
import com.courseplatform.recommendationservice.repository.RatingRepository
import com.courseplatform.recommendationservice.repository.RecommendationRepository
import com.courseplatform.recommendationservice.routes.recommendationRoutes
import com.courseplatform.recommendationservice.service.RecommendationService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val logger = LoggerFactory.getLogger("Application")
    
    // Initialize database
    DatabaseConfig.init(environment)
    
    // Create HTTP client for inter-service communication
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    // Initialize repositories
    val ratingRepository = RatingRepository()
    val recommendationRepository = RecommendationRepository()
    val courseCategoryRepository = CourseCategoryRepository()
    
    // Get config values
    val courseServiceUrl = environment.config.property("services.course-service-url").getString()
    val kafkaBootstrapServers = environment.config.property("kafka.bootstrap-servers").getString()
    val kafkaGroupId = environment.config.property("kafka.group-id").getString()
    val kafkaTopic = environment.config.property("kafka.topic").getString()
    
    // Initialize service
    val recommendationService = RecommendationService(
        ratingRepository,
        recommendationRepository,
        courseCategoryRepository,
        httpClient,
        courseServiceUrl
    )
    
    // Initialize Kafka consumer
    val kafkaConsumer = RatingEventConsumer(
        bootstrapServers = kafkaBootstrapServers,
        groupId = kafkaGroupId,
        topic = kafkaTopic,
        recommendationService = recommendationService
    )
    
    // Configure plugins
    install(ServerContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
    }
    
    install(CallLogging) {
        level = Level.INFO
    }
    
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("Internal server error: ${cause.message}", 500)
            )
        }
    }
    
    // Configure routing
    routing {
        recommendationRoutes(recommendationService)
    }
    
    // Start Kafka consumer
    launch {
        try {
            kafkaConsumer.start()
            logger.info("Kafka consumer started")
        } catch (e: Exception) {
            logger.error("Failed to start Kafka consumer: ${e.message}", e)
        }
    }
    
    // Sync courses on startup (with delay to allow other services to start)
    launch {
        try {
            kotlinx.coroutines.delay(10000) // Wait 10 seconds
            recommendationService.fetchAndCacheCoursesFromService()
        } catch (e: Exception) {
            logger.warn("Initial course sync failed: ${e.message}")
        }
    }
    
    // Graceful shutdown
    environment.monitor.subscribe(ApplicationStopping) {
        logger.info("Application stopping, closing Kafka consumer...")
        kafkaConsumer.stop()
        httpClient.close()
    }
    
    logger.info("Recommendation Service started on port ${environment.config.property("ktor.deployment.port").getString()}")
}
