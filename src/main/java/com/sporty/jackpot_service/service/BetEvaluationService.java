package com.sporty.jackpot_service.service;

import com.sporty.jackpot_service.dto.EvaluateBetRequest;
import com.sporty.jackpot_service.dto.EvaluationResult;
import com.sporty.jackpot_service.exception.ProcessingConflictException;
import com.sporty.jackpot_service.model.Jackpot;
import com.sporty.jackpot_service.model.JackpotContribution;
import com.sporty.jackpot_service.model.JackpotReward;
import com.sporty.jackpot_service.repository.JackpotContributionRepository;
import com.sporty.jackpot_service.repository.JackpotRepository;
import com.sporty.jackpot_service.repository.JackpotRewardRepository;
import com.sporty.jackpot_service.service.reward.RewardStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BetEvaluationService {

    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotRewardRepository rewardRepository;
    private final JackpotStrategyFactory strategyFactory;

    @Transactional
    public EvaluationResult evaluateBet(EvaluateBetRequest payload) {
        JackpotContribution contribution = contributionRepository.findByBetIdWithWriteLock(payload.betId())
                .orElseThrow(() -> new ProcessingConflictException(
                        "Evaluation rejected: Bet ID " + payload.betId() + " does not exist or has not been processed yet."));
        if (contribution.isEvaluated()) {
            throw new ProcessingConflictException("Evaluation rejected: Bet ID " + payload.betId() + " has already been evaluated.");
        }

        Jackpot jackpot = jackpotRepository.findByJackpotIdWithWriteLock(contribution.getJackpotId())
                .orElseThrow(() -> new IllegalArgumentException("Jackpot not found for ID: " + contribution.getJackpotId()));

        if (rewardRepository.existsByBetId(payload.betId())) {
            throw new ProcessingConflictException("Evaluation rejected: Bet ID " + payload.betId() + " already has a reward record.");
        }

        RewardStrategy rewardStrategy = strategyFactory.getRewardStrategy(jackpot.getRewardStrategy());

        boolean won = rewardStrategy.evaluateWin(jackpot.getCurrentBalance());

        BigDecimal payoutAmount = BigDecimal.ZERO;

        if (won) {
            // Winner takes the current pool balance
            payoutAmount = jackpot.getCurrentBalance();

            JackpotReward rewardRecord = new JackpotReward(
                    payload.betId(),
                    contribution.getUserId(),
                    jackpot.getJackpotId(),
                    payoutAmount
            );
            rewardRepository.save(rewardRecord);

            // Reset jackpot back to seed/base amount
            jackpot.resetToBase();
        }

        contribution.markEvaluated();
        contributionRepository.save(contribution);

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
