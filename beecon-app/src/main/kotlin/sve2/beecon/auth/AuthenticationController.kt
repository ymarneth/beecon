package sve2.beecon.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException


@RestController
@RequestMapping("/api")
class AuthenticationController {

    @GetMapping("/logout")
    @Throws(IOException::class)
    fun logout(request: HttpServletRequest?, response: HttpServletResponse) {
        SecurityContextLogoutHandler().logout(request, response, null)

        val keycloakLogoutUrl = "http://localhost:8081/realms/beecon/protocol/openid-connect/logout"

        response.sendRedirect(keycloakLogoutUrl)
    }
}
