package sve2.beecon.sensor_reading

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class SensorReadingListener(
    private val objectMapper: ObjectMapper,
    private val sensorReadingRepository: SensorReadingRepository,
    private val sensorReadingSubscriptionController: SensorReadingSubscriptionController
) {
    @RabbitListener(queues = ["sensor.readings"])
    fun handleSensorReadingMessage(message: String) {
        try {
            val readingDto = objectMapper.readValue(message, SensorReadingDto::class.java)

            val reading = SensorReading(
                hiveId = readingDto.hiveId,
                temperature = readingDto.temperature,
                humidity = readingDto.humidity,
                co2Emission = readingDto.co2Emission,
                precipitation = readingDto.precipitation,
                createdAt = readingDto.createdAt ?: Instant.now()
            )

            sensorReadingRepository.save(reading)
            println("Saved sensor reading from hive ${reading.hiveId}")
            sensorReadingSubscriptionController.publish(reading)
            println("Published sensor reading to subscribers: $reading")

        } catch (ex: Exception) {
            println("Failed to process sensor reading message: $ex")
        }
    }
}

data class SensorReadingDto(
    val hiveId: String,
    val temperature: Double,
    val humidity: Double,
    val co2Emission: Double,
    val precipitation: Double,
    val createdAt: Instant? = null
)
