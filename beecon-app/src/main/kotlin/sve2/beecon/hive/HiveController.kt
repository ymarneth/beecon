package sve2.beecon.hive

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/hives")
class HiveController(
    private val hiveRepository: HiveRepository
) {

    @GetMapping
    @Operation(
        summary = "Get all hives for the authenticated user",
        description = "Returns a list of hives associated with the authenticated user.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "List of hives",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Example Response for successfully reading all hives of a user",
                                value = """
                                    [
                                        {
                                            "id": "hive-1",
                                            "userId": "user-1",
                                            "name": "Meadow Hive",
                                            "createdAt": "2025-04-28T15:30:00Z"
                                        },
                                        {
                                            "id": "hive-2",
                                            "userId": "user-1",
                                            "name": "Rooftop Hive",
                                            "createdAt": "2025-04-28T15:35:00Z"
                                        }
                                    ]
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun getHives(
        authentication: Authentication,
    ): ResponseEntity<Any>  {
        val user = authentication.principal as org.springframework.security.oauth2.core.user.DefaultOAuth2User
        var userId = user.attributes["sub"] as String

        val hives = hiveRepository.findByUserId(userId)

        return ResponseEntity.ok(hives)
    }

    @GetMapping("/{hiveId}/add")
    @Operation(
        summary = "Link the hive with the given id to the authenticated user",
        description = "Returns the newly linked hive to the authenticated user.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Hives",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Example Response for successfully linking a hive to a user",
                                value = """
                                    {
                                        "id": "hive-1",
                                        "userId": "user-1",
                                        "name": "Meadow Hive",
                                        "createdAt": "2025-04-28T15:35:00Z"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun addHive(
        @PathVariable hiveId: String,
        authentication: Authentication,
    ): ResponseEntity<Any>  {
        val user = authentication.principal as org.springframework.security.oauth2.core.user.DefaultOAuth2User
        var userId = user.attributes["sub"] as String

        val newHive = Hive(
            id = hiveId,
            userId = userId,
            name = hiveId,
            createdAt = Instant.now()
        )

        hiveRepository.save(newHive)

        return ResponseEntity.ok(newHive)
    }
}
