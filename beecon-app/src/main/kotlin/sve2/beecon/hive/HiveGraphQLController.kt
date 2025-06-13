package sve2.beecon.hive

import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.stereotype.Controller
import sve2.beecon.sensor_reading.AggregatedSensorReading
import sve2.beecon.sensor_reading.SensorReading
import sve2.beecon.sensor_reading.SensorReadingRepository
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit


@Controller
class HiveGraphQLController(
    private val hiveRepository: HiveRepository,
    private val sensorReadingRepository: SensorReadingRepository
) {
    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun hives(
        authentication: Authentication
    ): List<Hive> {
        val user = authentication.principal as DefaultOAuth2User
        val userId = user.attributes["sub"] as String

        return hiveRepository.findByUserId(userId)
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun addHive(
        @Argument id: String,
        @Argument name: String?,
        authentication: Authentication
    ): Hive {
        val user = authentication.principal as DefaultOAuth2User
        val userId = user.attributes["sub"] as String

        val newHive = Hive(
            id = id,
            userId = userId,
            name = name ?: id,
            createdAt = Instant.now()
        )

        hiveRepository.save(newHive)

        return newHive
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun hiveSensorReadings(
        @Argument hiveIds: List<String>,
        authentication: Authentication
    ): List<Hive> {
        val user = authentication.principal as DefaultOAuth2User
        val userId = user.attributes["sub"] as String

        val hives = mutableListOf<Hive>()

        for (hiveId in hiveIds) {
            val hive = hiveRepository.findById(hiveId).orElse(null)
                ?: throw HiveNotFoundException("Hive with id '$hiveId' not found")

            if (hive.userId != userId) {
                throw AccessDeniedException("You do not have access to this hive")
            }

            hives.add(hive)
        }

        return hives
    }

    @SchemaMapping
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun rawSensorReadings(
        hive: Hive,
        @Argument from: Instant?,
        @Argument to: Instant?
    ): List<SensorReading> {
        val from = from ?: Instant.EPOCH
        val to = to ?: Instant.now()

        return sensorReadingRepository.findByHiveIdAndCreatedAtBetween(
            hive.id,
            from,
            to,
            Sort.by(Sort.Order.asc("createdAt"))
        )
    }

    @SchemaMapping
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun sensorReadings(
        hive: Hive,
        @Argument from: Instant?,
        @Argument to: Instant?,
        @Argument interval: AggregationInterval?)
    : List<AggregatedSensorReading> {
        val fromNotNull = from ?: Instant.EPOCH
        val toNotNull = to ?: Instant.now()
        val intervalNotNull = interval ?: AggregationInterval.MINUTE

        val sort = Sort.by(Sort.Order.asc("createdAt"))
        val sensorReadings = sensorReadingRepository.findByHiveIdAndCreatedAtBetween(hive.id, fromNotNull, toNotNull, sort)
        val aggrSensorReadings = mutableListOf<AggregatedSensorReading>()

        val groupedReadings = sensorReadings.groupBy { reading ->
            val zoned = reading.createdAt.atZone(ZoneId.of("UTC"))
            when (intervalNotNull) {
                AggregationInterval.MINUTE -> zoned.truncatedTo(ChronoUnit.MINUTES).toInstant()
                AggregationInterval.FIVE_MINUTES -> {
                    val minute = (zoned.minute / 5) * 5
                    zoned.withMinute(minute).withSecond(0).withNano(0).toInstant()
                }
                AggregationInterval.HALF_HOUR -> {
                    val minute = if (zoned.minute < 30) 0 else 30
                    zoned.withMinute(minute).withSecond(0).withNano(0).toInstant()
                }
                AggregationInterval.HOUR -> zoned.truncatedTo(ChronoUnit.HOURS).toInstant()
                AggregationInterval.DAY -> zoned.toLocalDate().atStartOfDay(ZoneId.of("UTC")).toInstant()
                AggregationInterval.WEEK -> zoned
                    .with(DayOfWeek.MONDAY)
                    .toLocalDate()
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                AggregationInterval.MONTH -> zoned
                    .withDayOfMonth(1)
                    .toLocalDate()
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                AggregationInterval.SIX_MONTHS -> {
                    val month = zoned.monthValue
                    val baseMonth = if (month <= 6) 1 else 7 // Jan or Jul
                    val start = zoned.withMonth(baseMonth).withDayOfMonth(1)
                    start.toLocalDate().atStartOfDay(zoned.zone).toInstant()
                }
            }
        }

        for (entry in groupedReadings) {
            val readings = entry.value
            val avgTemperature = readings.map { it.temperature }.average()
            val avgHumidity = readings.map { it.humidity }.average()
            val avgCo2Emission = readings.map { it.co2Emission }.average()
            val avgPrecipitation = readings.map { it.precipitation }.average()

            val minTemperatur = readings.minOfOrNull { it.temperature } ?: 0.0
            val minHumidity = readings.minOfOrNull { it.humidity } ?: 0.0
            val minCo2Emission = readings.minOfOrNull { it.co2Emission } ?: 0.0
            val minPrecipitation = readings.minOfOrNull { it.precipitation } ?: 0.0

            val maxTemperature = readings.maxOfOrNull { it.temperature } ?: 0.0
            val maxHumidity = readings.maxOfOrNull { it.humidity } ?: 0.0
            val maxCo2Emission = readings.maxOfOrNull { it.co2Emission } ?: 0.0
            val maxPrecipitation = readings.maxOfOrNull { it.precipitation } ?: 0.0

            aggrSensorReadings.add(AggregatedSensorReading(
                avg =
                    SensorReading(
                        hiveId = hive.id,
                        temperature = avgTemperature,
                        humidity = avgHumidity,
                        co2Emission = avgCo2Emission,
                        precipitation = avgPrecipitation,
                        createdAt = entry.key
                    ),
                min =
                    SensorReading(
                    hiveId = hive.id,
                    temperature = minTemperatur,
                    humidity = minHumidity,
                    co2Emission = minCo2Emission,
                    precipitation = minPrecipitation,
                    createdAt = entry.key
                ),
                max =
                    SensorReading(
                        hiveId = hive.id,
                        temperature = maxTemperature,
                        humidity = maxHumidity,
                        co2Emission = maxCo2Emission,
                        precipitation = maxPrecipitation,
                        createdAt = entry.key
                    )
            ))
        }

        return aggrSensorReadings
    }
}


class HiveNotFoundException(message: String) : RuntimeException(message)

class AccessDeniedException(message: String) : RuntimeException(message)
