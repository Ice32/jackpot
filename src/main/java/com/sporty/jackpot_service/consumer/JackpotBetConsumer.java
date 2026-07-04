package com.sporty.jackpot_service.consumer;

import com.sporty.jackpot_service.dto.request.BetPayload;
import com.sporty.jackpot_service.model.Jackpot;
import com.sporty.jackpot_service.model.JackpotContribution;
import com.sporty.jackpot_service.repository.JackpotContributionRepository;
import com.sporty.jackpot_service.repository.JackpotRepository;
import com.sporty.jackpot_service.service.JackpotStrategyFactory;
import com.sporty.jackpot_service.service.contribution.ContributionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class JackpotBetConsumer {

    private static final Logger log = LoggerFactory.getLogger(JackpotBetConsumer.class);

    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotStrategyFactory strategyFactory;

    public JackpotBetConsumer(JackpotRepository jackpotRepository,
                              JackpotContributionRepository contributionRepository,
                              JackpotStrategyFactory strategyFactory) {
        this.jackpotRepository = jackpotRepository;
        this.contributionRepository = contributionRepository;
        this.strategyFactory = strategyFactory;
    }

    /**
     * Listens to the 'jackpot-bets' Kafka topic verbatim from requirements.
     * Handles payload parsing automatically using the spring-kafka JSON deserializer layout.
     */
    @KafkaListener(
            topics = "jackpot-bets",
            groupId = "jackpot-contribution-group",
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"auto.offset.reset=earliest"}
    )
    @Transactional // Guarantees atomicity: locks, state updates, and logs succeed or fail together
    public void consumeJackpotBet(BetPayload payload) {
        log.info("Received background bet event processing task for Bet ID: {}", payload.betId());

        // 1. Idempotency Check: Prevent duplicate ledger processing if Kafka re-delivers the message
        if (contributionRepository.existsByBetId(payload.betId())) {
            log.warn("Idempotency match triggered. Bet ID [{}] already registered in ledger. Skipping task.", payload.betId());
            return;
        }

        // 2. Fetch Parent State with an explicit Row-Level Pessimistic Write Lock
        // This queues concurrent consumer threads linearly, protecting the balance integrity.
        Jackpot jackpot = jackpotRepository.findByJackpotIdWithWriteLock(payload.jackpotId())
                .orElseThrow(() -> new IllegalArgumentException("Target Jackpot jackpotId=" + payload.jackpotId() + "was not found in database records."));

        // 3. Resolve calculation framework dynamically based on active configuration enums
        ContributionStrategy strategy = strategyFactory.getContributionStrategy(jackpot.getContributionStrategy());

        // 4. Compute fractional stake allocation using safe scale models
        BigDecimal calculatedContribution = strategy.calculateContribution(payload.betAmount(), jackpot.getCurrentBalance());

        log.debug("Applying {} allocation logic. Base: {}, Result: {}",
                jackpot.getContributionStrategy(), payload.betAmount(), calculatedContribution);

        // 5. Mutate state totals inside entity boundaries
        jackpot.incrementBalance(calculatedContribution);
        jackpotRepository.save(jackpot); // Flushes lock and updates version metrics safely

        // 6. Write to the transactional logging ledger
        JackpotContribution ledgerRecord = new JackpotContribution(
                payload.betId(),
                payload.userId(),
                payload.jackpotId(),
                payload.betAmount(),
                calculatedContribution,
                jackpot.getCurrentBalance()

        );
        contributionRepository.save(ledgerRecord);

        log.info("Successfully updated pool balance tracking and committed ledger for Bet ID: {}. New Balance: {}",
                payload.betId(), jackpot.getCurrentBalance());
    }
}
