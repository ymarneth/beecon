package sve2.beecon

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun createQueue(): Queue {
        println("Creating RabbitMQ queue")
        return Queue("sensor.readings")
    }

    @Bean
    fun createBindingBetweenQueueAndMqttTopic(): Binding {
        return Binding("sensor.readings", Binding.DestinationType.QUEUE, "amq.topic", "sensor.#", null)
    }
}
