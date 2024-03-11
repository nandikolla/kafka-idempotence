package producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import producer.model.ClaimEvent;
import producer.producer.ClaimEventProducer;

@RestController
@Slf4j
public class ClaimEventController {

    @Autowired
    ClaimEventProducer claimEventProducer;

    @PostMapping("/v1/claimEvent")
    public ResponseEntity<?> postLibraryEvent(@RequestBody ClaimEvent claimEvent) throws JsonProcessingException {

        //invoke kafka producer
        claimEventProducer.sendClaimEvent_ProducerRecord(claimEvent);
        //libraryEventProducer.sendLibraryEvent(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(claimEvent);
    }

    //PUT
    @PutMapping("/v1/claimEvent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody ClaimEvent claimEvent) throws JsonProcessingException {


        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(claimEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        claimEventProducer.sendClaimEvent_ProducerRecord(claimEvent);
        log.info("after produce call");
        return ResponseEntity.status(HttpStatus.OK).body(claimEvent);
    }

    private static ResponseEntity<String> validateLibraryEvent(ClaimEvent claimEvent) {
        if (claimEvent.getClaimId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");
        }

        return null;
    }


}