package sve2.beecon.hive

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "hives")
data class Hive (
    @Id
    val id: String = UUID.randomUUID().toString(),

    val userId: String = "",

    val name: String = "",

    val createdAt: Instant = Instant.now()
)
