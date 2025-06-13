import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI().info(
            Info()
                .title("Beecon API")
                .version("1.0")
                .description("This is the API documentation for the Beecon Spring Boot Kotlin application")
        )
    }
}