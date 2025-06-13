package sve2.beecon.sensor_reading

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

@RestController
@RequestMapping("/api/me")
class UserController {
    @GetMapping
    @Operation(
        summary = "Get information of the authenticated user",
        description = "This endpoint retrieves the authenticated user's personal information, including their ID, email, name, and username.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "User information retrieved successfully",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "Example Response for reading user data provided by Keycloak of the authenticated user.",
                                value = """
                                    {
                                        "id": "user-1",
                                        "email": "user@example.com",
                                        "firstname": "John",
                                        "lastname": "Doe",
                                        "name": "John Doe",
                                        "username": "johndoe"
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
    fun getUserInfo(
        authentication: Authentication
    ): ResponseEntity<UserInfo> {
        val user = authentication.principal as DefaultOAuth2User
        val id = user.attributes["sub"] as String
        val username = user.attributes["preferred_username"] as String
        val firstname = user.attributes["given_name"] as String
        val lastname = user.attributes["family_name"] as String
        val name = user.attributes["name"] as String
        val email = user.attributes["email"] as String

        val userInfo = UserInfo(
            id = id,
            email = email,
            firstname = firstname,
            lastname = lastname,
            name = name,
            username = username
        )

        return ResponseEntity.ok(userInfo)
    }
}

data class UserInfo(
    val id: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val name: String,
    val username: String
)



