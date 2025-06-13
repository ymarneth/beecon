package sve2.beecon.sensor_reading

import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Controller
class SensorReadingSubscriptionController {

    private val sink: Sinks.Many<SensorReading> = Sinks.many().multicast().onBackpressureBuffer()

    fun publish(sensorReading: SensorReading) {
        sink.tryEmitNext(sensorReading)
    }

    @SubscriptionMapping
    fun sensorReadingAdded(@Argument hiveId: String): Flux<SensorReading> {
        return sink.asFlux().filter { it.hiveId == hiveId }
    }
}
