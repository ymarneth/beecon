package sve2.beecon.hive
import org.springframework.data.jpa.repository.JpaRepository

interface HiveRepository : JpaRepository<Hive, String> {
    fun findByUserId(userId: String): List<Hive>
}
