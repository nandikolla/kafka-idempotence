package producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import producer.model.ClaimEvent;


@Service
@Slf4j
public class ClaimEventService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<Integer, Object> kafkaTemplate;

    public void processLibraryEvent(ConsumerRecord<Integer, ClaimEvent> consumerRecord) throws JsonProcessingException {
        ClaimEvent claimEvent = consumerRecord.value();
        log.info("libraryEvent : {} ", claimEvent);

        if(claimEvent.getClaimId() != null && ( claimEvent.getClaimId() == 999 )){
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }
        save(claimEvent);


    }

    private void save(ClaimEvent claimEvent) {
        log.info("Successfully Persisted the claim Event {} ", claimEvent);
    }

    private void validate(ClaimEvent claimEvent) {

        if (claimEvent.getClaimId() == null || claimEvent.getProduct().getProductId() == null) {
            throw new IllegalArgumentException("Library Event Id is missing");
        }

        log.info("Validation is successful for the library Event : {} ", claimEvent);
    }



}
