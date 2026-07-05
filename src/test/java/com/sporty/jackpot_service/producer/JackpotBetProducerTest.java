package com.sporty.jackpot_service.producer;

import com.sporty.jackpot_service.UnitTest;
import com.sporty.jackpot_service.dto.SubmitBetRequest;
import com.sporty.jackpot_service.exception.BetSubmissionUnavailableException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@UnitTest
class JackpotBetProducerTest {

    @Mock
    private KafkaTemplate<String, SubmitBetRequest> kafkaTemplate;

    @InjectMocks
    private JackpotBetProducer producer;

    @Test
    void publishBet_WhenSendCannotBeQueued_ThrowsSubmissionUnavailable() {
        var payload = new SubmitBetRequest("bet-1", "jackpot-1", "user-1", BigDecimal.TEN);
        var sendFailure = new IllegalStateException("producer unavailable");

        when(kafkaTemplate.send("jackpot-bets", payload)).thenThrow(sendFailure);

        assertThatThrownBy(() -> producer.publishBet(payload))
                .isInstanceOf(BetSubmissionUnavailableException.class)
                .hasMessage("Bet submission is temporarily unavailable.")
                .hasCause(sendFailure);
    }
}
