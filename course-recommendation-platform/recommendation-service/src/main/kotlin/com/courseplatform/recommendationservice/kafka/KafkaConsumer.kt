package com.courseplatform.recommendationservice.kafka

import com.courseplatform.recommendationservice.model.RatingEventDTO
import com.courseplatform.recommendationservice.service.RecommendationService
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

class RatingEventConsumer(
    private val bootstrapServers: String,
    private val groupId: String,
    private val topic: String,
    private val recommendationService: RecommendationService
) {
    private val logger = LoggerFactory.getLogger(RatingEventConsumer::class.java)
    private val gson = Gson()
    private var consumer: KafkaConsumer<String, String>? = null
    private var running = false
    private var job: Job? = null
    
    private fun createConsumer(): KafkaConsumer<String, String> {
        val props = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
            put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
        }
        return KafkaConsumer(props)
    }
    
    fun start() {
        running = true
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                consumer = createConsumer()
                consumer?.subscribe(listOf(topic))
                logger.info("Kafka consumer started, subscribed to topic: $topic")
                
                while (running) {
                    try {
                        val records = consumer?.poll(Duration.ofMillis(1000))
                        records?.forEach { record ->
                            try {
                                logger.debug("Received message: key=${record.key()}, value=${record.value()}")
                                val event = gson.fromJson(record.value(), RatingEventDTO::class.java)
                                logger.info("Processing rating event: userId=${event.userId}, courseId=${event.courseId}, rating=${event.rating}")
                                recommendationService.processRatingEvent(event)
                            } catch (e: Exception) {
                                logger.error("Error processing message: ${e.message}", e)
                            }
                        }
                    } catch (e: Exception) {
                        if (running) {
                            logger.error("Error polling messages: ${e.message}", e)
                            delay(1000)
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Kafka consumer error: ${e.message}", e)
            } finally {
                consumer?.close()
                logger.info("Kafka consumer stopped")
            }
        }
    }
    
    fun stop() {
        running = false
        job?.cancel()
        consumer?.wakeup()
    }
}
