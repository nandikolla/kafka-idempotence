package producer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import producer.model.ClaimEvent;
import producer.service.ClaimEventService;

@Component
@Slf4j
public class ClaimEventsConsumer {

    @Autowired
    private ClaimEventService claimEventService;

    @KafkaListener(topics = {"claim-events"}, groupId = "claim-consumer-group-1")
    public void onMessage(ConsumerRecord<Integer, ClaimEvent> consumerRecord) throws JsonProcessingException {
        claimEventService.processLibraryEvent(consumerRecord);
        log.info("Consumer Record: {}", consumerRecord);
    }

    /**
     *  ----- consumer group and partition with intial offset  ----
     *
    @KafkaListener(groupId = "claim-consumer-group-1",
            topicPartitions = @TopicPartition(topic = "claim-events",
                    partitionOffsets = {
                            @PartitionOffset(partition = "0", initialOffset = "0"),
                            @PartitionOffset(partition = "2", initialOffset = "0")}))
     */
    public void onMessage_PartitionIntialOffset(ConsumerRecord<Integer, ClaimEvent> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }


    /**
     * ----- consumer group and partition with no intial offset  ----
     *
    @KafkaListener(topicPartitions = @TopicPartition(topic = "claim-events", partitions = { "0", "1" }))
     */
    public void onMessage_PartitionNoOffset(ConsumerRecord<Integer, ClaimEvent> consumerRecord) {
        log.info("Consumer Record: {}", consumerRecord);
    }
}
