package com.sporty.jackpot_service.producer;

import com.sporty.jackpot_service.dto.SubmitBetRequest;
import com.sporty.jackpot_service.exception.BetSubmissionUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JackpotBetProducer {
    private static final String TOPIC_NAME = "jackpot-bets";

    private final KafkaTemplate<String, SubmitBetRequest> kafkaTemplate;

    public void publishBet(SubmitBetRequest payload) {
        // Keep endpoint high-throughput by not blocking on broker acknowledgement. We still fail
        // fast if the publish cannot be queued locally; production durability/retries would normally
        // be handled with an outbox, which is intentionally out of scope for this assignment.
        try {
            kafkaTemplate.send(TOPIC_NAME, payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Publishing failed for Bet ID: {}", payload.betId(), exception);
                        } else {
                            log.debug("Successfully appended to partition [{}], offset [{}]",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception ex) {
            log.error("Publishing could not be queued for Bet ID: {}", payload.betId(), ex);
            throw new BetSubmissionUnavailableException("Bet submission is temporarily unavailable.", ex);
        }
    }
}
