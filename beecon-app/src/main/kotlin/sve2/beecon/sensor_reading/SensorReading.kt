package sve2.beecon.sensor_reading

import java.util.UUID
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "sensor_readings")
data class SensorReading (
    @Id
    val id: String = UUID.randomUUID().toString(),

    val hiveId: String = "",

    val temperature: Double = 0.0,

    val humidity: Double = 0.0,

    val co2Emission: Double = 0.0,

    val precipitation: Double = 0.0,

    val createdAt: Instant = Instant.now()
)