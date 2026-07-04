package com.sporty.jackpot_service.producer;

import com.sporty.jackpot_service.dto.SubmitBetRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JackpotBetProducer {
    private static final String TOPIC_NAME = "jackpot-bets";

    private final KafkaTemplate<String, SubmitBetRequest> kafkaTemplate;

    public JackpotBetProducer(KafkaTemplate<String, SubmitBetRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBet(SubmitBetRequest payload) {
        kafkaTemplate.send(TOPIC_NAME, payload)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Publishing failed for Bet ID: " + payload.betId(), exception);
                    } else {
                        log.info("Successfully appended to partition [{}], offset [{}]",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
