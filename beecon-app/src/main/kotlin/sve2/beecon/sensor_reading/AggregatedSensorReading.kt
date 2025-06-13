package sve2.beecon.sensor_reading

import java.util.UUID
import jakarta.persistence.*
import java.time.Instant

data class AggregatedSensorReading (
    val max: SensorReading,
    val min: SensorReading,
    val avg: SensorReading,
)