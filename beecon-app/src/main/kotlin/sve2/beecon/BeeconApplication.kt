package sve2.beecon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@SpringBootApplication
@EnableScheduling
@EnableMethodSecurity
class BeeconApplication

fun main(args: Array<String>) {
    runApplication<BeeconApplication>(*args)
}
