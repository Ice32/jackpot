package com.sporty.jackpot_service.service;

import com.sporty.jackpot_service.dto.EvaluateBetRequest;
import com.sporty.jackpot_service.dto.EvaluationResult;
import com.sporty.jackpot_service.model.Jackpot;
import com.sporty.jackpot_service.repository.JackpotRepository;
import com.sporty.jackpot_service.service.reward.RewardStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BetEvaluationService {

    private final JackpotRepository jackpotRepository;
    private final JackpotStrategyFactory strategyFactory;

    @Transactional
    public EvaluationResult evaluateBet(EvaluateBetRequest payload) {
        // 1. Fetch jackpot with a pessimistic write lock to handle concurrency safely
        Jackpot jackpot = jackpotRepository.findByJackpotIdWithWriteLock(payload.jackpotId())
                .orElseThrow(() -> new IllegalArgumentException("Jackpot not found for ID: " + payload.jackpotId()));

        // 2. Resolve the reward strategy via the factory
        RewardStrategy rewardStrategy = strategyFactory.getRewardStrategy(jackpot.getRewardStrategy());

        // 3. Evaluate if the bet is a winner
        boolean won = rewardStrategy.evaluateWin(jackpot.getCurrentBalance());

        BigDecimal payoutAmount = BigDecimal.ZERO;

        if (won) {
            // Winner takes the current pool balance
            payoutAmount = jackpot.getCurrentBalance();
            // Reset jackpot back to seed/base amount
            jackpot.resetToBase();
        }

        // Save the updated state (explicit call, though managed by Hibernate transaction)
        jackpotRepository.save(jackpot);

        return new EvaluationResult(
                payload.betId(),
                won,
                payoutAmount,
                jackpot.getCurrentBalance()
        );
    }
}
