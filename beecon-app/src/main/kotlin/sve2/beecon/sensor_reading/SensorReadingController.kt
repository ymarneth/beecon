package sve2.beecon.sensor_reading

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import sve2.beecon.hive.HiveRepository
import java.time.Instant

@RestController
@RequestMapping("/api/hives")
class SensorReadingController(
    private val hiveRepository: HiveRepository,
    private val sensorReadingRepository: SensorReadingRepository
) {

    @GetMapping("/{hiveId}/readings")
    @Operation(
        summary = "Get sensor readings for a specific hive",
        description = "Fetches a list of sensor readings for the given hive after the specified date-time.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of sensor readings",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Example Response",
                                value = """
                                    [
                                        {
                                            "id": "reading-1",
                                            "hiveId": "hive-1",
                                            "temperature": 22.5,
                                            "humidity": 0.45,
                                            "co2Emission": 16.5,
                                            "precipitation": 0.1,
                                            "createdAt": "2025-04-28T15:30:00Z"
                                        },
                                        {
                                            "id": "reading-2",
                                            "sensorId": "sensor-1",
                                            "temperature": 21.7,
                                            "humidity": 0.50,
                                            "co2Emission": 15.8,
                                            "precipitation": 0.2,
                                            "createdAt": "2025-04-28T15:35:00Z"
                                        }
                                    ]
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Hive not found",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "403",
                description = "Access forbidden to the hive",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun getReadings(
        @PathVariable hiveId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) after: Instant,
        authentication: Authentication,
    ): ResponseEntity<Any>  {
        val user = authentication.principal as org.springframework.security.oauth2.core.user.DefaultOAuth2User
        val userId = user.attributes["sub"] as String

        val hive = hiveRepository.findById(hiveId).orElse(null)

        if (hive == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Sensor with id '$hiveId' not found")
        }

        if (hive.userId != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You do not have access to this sensor")
        }

        val sort = Sort.by(Sort.Order.asc("createdAt"))
        val readings = sensorReadingRepository.findByHiveIdAndCreatedAtAfter(hiveId, after, sort)
        return ResponseEntity.ok(readings)
    }
}
