package sve2.beecon.sensor_reading
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface SensorReadingRepository : JpaRepository<SensorReading, String> {
    fun findByHiveIdAndCreatedAtAfter(hiveId: String, after: Instant, sort: Sort): List<SensorReading>
    fun findByHiveIdAndCreatedAtBetween(hiveId: String, from: Instant, to: Instant, sort: Sort): List<SensorReading>
    fun findByHiveId(hiveId: String, sort: Sort): List<SensorReading>
}
