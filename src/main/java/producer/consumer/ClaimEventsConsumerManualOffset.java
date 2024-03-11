package producer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import producer.model.ClaimEvent;
import producer.service.ClaimEventService;

//@Component
@Slf4j
public class ClaimEventsConsumerManualOffset implements AcknowledgingMessageListener<Integer, ClaimEvent> {

    @Autowired
    private ClaimEventService claimEventService;

    @Override
    @KafkaListener(topics = "claim-events", groupId = "claim-consumer-group-1")
    public void onMessage(ConsumerRecord<Integer, ClaimEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("Consumer Record: {}", consumerRecord);
        acknowledgment.acknowledge();
    }
}
