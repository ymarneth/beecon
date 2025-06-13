package sve2.beecon

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser

import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/**").authenticated()
                    .requestMatchers("/swagger-ui.html", "/v3/api-docs/**").authenticated()
                    .requestMatchers("/", "/index.html", "/assets/**", "/static/**").authenticated()
                    .requestMatchers(
                        "/graphql",
                        "/graphiql",
                        "/graphiql/**",
                        "/webjars/**",
                        "/static/**",
                        "/assets/**"
                    ).authenticated()
                    .requestMatchers(HttpMethod.POST, "/graphql").authenticated()
                    .anyRequest().permitAll()
            }
            .csrf { it.ignoringRequestMatchers("/graphql") } // Is needed so in order for /graphiql to be able to load the schema (should be disabled in production)
            .oauth2Login {
                println("Configuring OAuth2 Login")
                it.userInfoEndpoint {
                    it.oidcUserService(customOidcUserService())
                }
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("http://localhost:8081/realms/beecon/protocol/openid-connect/logout?redirect_uri=http://localhost:8080/")
            }

        return http.build()
    }

    fun customOidcUserService(): OidcUserService {
        val delegate = OidcUserService()

        return object : OidcUserService() {
            override fun loadUser(userRequest: OidcUserRequest): OidcUser {
                val oidcUser = delegate.loadUser(userRequest)

                // Extract roles from the token claims
                val claims = oidcUser.claims

                val realmRoles = (claims["realm_access"] as? Map<*, *>)?.get("roles") as? List<*> ?: emptyList<Any>()
                val clientRoles = (claims["resource_access"] as? Map<*, *>)?.get("beecon-app")?.let {
                    (it as? Map<*, *>)?.get("roles") as? List<*>
                } ?: emptyList<Any>()

                val mappedAuthorities = mutableSetOf<GrantedAuthority>()
                (realmRoles + clientRoles).forEach { role ->
                    mappedAuthorities.add(SimpleGrantedAuthority("ROLE_${role.toString()}"))
                }

                // Combine extracted roles with original authorities if needed:
                val allAuthorities = mappedAuthorities + oidcUser.authorities

                return DefaultOidcUser(allAuthorities, oidcUser.idToken, oidcUser.userInfo)
            }
        }
    }
}
