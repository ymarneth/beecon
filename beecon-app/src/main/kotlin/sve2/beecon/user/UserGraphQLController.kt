package sve2.beecon.user

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.stereotype.Controller

@Controller
class UserGraphQLController {

    @QueryMapping
    @PreAuthorize("hasAuthority('ROLE_user')")
    fun me(authentication: Authentication): User {
        val user = authentication.principal as DefaultOAuth2User

        return User(
            id = user.attributes["sub"] as String,
            email = user.attributes["email"] as String,
            firstname = user.attributes["given_name"] as String,
            lastname = user.attributes["family_name"] as String,
            name = user.attributes["name"] as String,
            username = user.attributes["preferred_username"] as String
        )
    }
}