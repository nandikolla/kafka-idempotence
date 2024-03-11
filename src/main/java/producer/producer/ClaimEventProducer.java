package producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import producer.model.ClaimEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ClaimEventProducer {

    @Value("${spring.kafka.topic}")
    public String topic;

    @Autowired
    private KafkaTemplate<Integer, Object> kafkaTemplate;

    public CompletableFuture<SendResult<Integer, Object>> sendClaimEvent_Async(ClaimEvent claimEvent) throws JsonProcessingException {

        var key = claimEvent.getClaimId();

        var completableFuture = kafkaTemplate.send(topic, key, claimEvent);

        return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, claimEvent, throwable);
            } else {
                handleSuccess(key, claimEvent, sendResult);
            }
        }));
    }

    public CompletableFuture<SendResult<Integer, Object>> sendClaimEvent_ProducerRecord(ClaimEvent claimEvent) throws JsonProcessingException {

        var key = claimEvent.getClaimId();
        var producerRecord = buildProducerRecord(key, claimEvent);

        var completableFuture = kafkaTemplate.send(producerRecord);

        return completableFuture.whenComplete(((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(key, claimEvent, throwable);
            } else {
                handleSuccess(key, claimEvent, sendResult);
            }
        }));
    }

    private ProducerRecord<Integer, Object> buildProducerRecord(Integer key, Object value) {
        List<Header> recordHeader = List.of(new RecordHeader("event-source", "library-event-producer".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, recordHeader);
    }

    private void handleSuccess(Integer key, Object value, SendResult<Integer, Object> sendResult) {
        log.info("Message sent successfully for the key: {} and the value: {}, partition is: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(Integer key, Object value, Throwable throwable) {
        log.error("Error sending message and exception is {}", throwable.getMessage(), throwable);
    }
}
