package producer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private List<String> bootstrapAddress;

    @Bean
    public ProducerFactory<?, ?> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        configProps.put(
          ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Enable idempotence
        /*
          by setting the enable.idempotence property to true, Kafka automatically manages unique Producer IDs and Sequence Numbers for each message.
          This ensures that messages are delivered exactly once, without any manual configuration needed for these IDs and numbers.
         */
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        /*
          ACKs: Must be set to all. This ensures that the producer waits for acknowledgments from all in-sync replicas.
         */
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        /*
          Must be greater than 0(zero). The default value of this configuration property is very high. It is equal to 2147483647, which is equal to Integer.MAX_VALUE
         */
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        /*
          This should be set to 5 or lower to maintain the order of message delivery.
         */
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


}