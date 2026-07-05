package com.sporty.jackpot_service.consumer;

import com.sporty.jackpot_service.dto.SubmitBetRequest;
import com.sporty.jackpot_service.model.Jackpot;
import com.sporty.jackpot_service.model.JackpotContribution;
import com.sporty.jackpot_service.repository.JackpotContributionRepository;
import com.sporty.jackpot_service.repository.JackpotRepository;
import com.sporty.jackpot_service.service.JackpotStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JackpotBetConsumer {

    private static final Logger log = LoggerFactory.getLogger(JackpotBetConsumer.class);

    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotStrategyFactory strategyFactory;

    @KafkaListener(
            topics = "jackpot-bets",
            groupId = "jackpot-contribution-group",
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"auto.offset.reset=earliest"}
    )
    @Transactional
    public void consumeJackpotBet(SubmitBetRequest payload) {
        log.debug("Received background bet event processing task for Bet ID: {}", payload.betId());

        // 1. Idempotency Check: Prevent duplicate ledger processing if Kafka re-delivers the message
        if (contributionRepository.existsByBetId(payload.betId())) {
            log.warn("Idempotency match triggered. Bet ID [{}] already registered in ledger. Skipping task.", payload.betId());
            return;
        }

        // 2. Fetch Parent State with an explicit Row-Level Pessimistic Write Lock
        // This queues concurrent consumer threads linearly, protecting the balance integrity.
        Jackpot jackpot = jackpotRepository.findByJackpotIdWithWriteLock(payload.jackpotId())
                .orElseThrow(() -> new IllegalArgumentException("Target Jackpot jackpotId=" + payload.jackpotId() + "was not found in database records."));


        // 3. Delegate to the core business logic
        JackpotContribution ledgerRecord = jackpot.contribute(payload, strategyFactory);
        contributionRepository.save(ledgerRecord);

        log.debug("Successfully updated pool balance tracking and committed ledger for Bet ID: {}. New Balance: {}",
                payload.betId(), jackpot.getCurrentBalance());
    }
}
