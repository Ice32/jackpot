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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BetEvaluationService {

    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotRewardRepository rewardRepository;
    private final JackpotStrategyFactory strategyFactory;

    @Transactional
    public EvaluationResult evaluateBet(EvaluateBetRequest payload) {
        String jackpotId = contributionRepository.findJackpotIdByBetId(payload.betId())
                .orElseThrow(() -> new ProcessingConflictException(
                        "Evaluation rejected: Bet ID " + payload.betId() + " does not exist or has not been processed yet."));

        Jackpot jackpot = jackpotRepository.findByJackpotIdWithWriteLock(jackpotId)
                .orElseThrow(() -> new IllegalArgumentException("Jackpot not found for ID: " + jackpotId));

        JackpotContribution contribution = contributionRepository.findByBetIdWithWriteLock(payload.betId())
                .orElseThrow(() -> new ProcessingConflictException(
                        "Evaluation rejected: Bet ID " + payload.betId() + " does not exist or has not been processed yet."));
        if (contribution.isEvaluated()) {
            throw new ProcessingConflictException("Evaluation rejected: Bet ID " + payload.betId() + " has already been evaluated.");
        }

        if (rewardRepository.existsByBetId(payload.betId())) {
            throw new ProcessingConflictException("Evaluation rejected: Bet ID " + payload.betId() + " already has a reward record.");
        }

        Optional<JackpotReward> rewardOptional = jackpot.evaluate(contribution, strategyFactory);

        // Save the updated state (explicit call, though managed by Hibernate transaction)
        rewardOptional.ifPresent(rewardRepository::save);

        return new EvaluationResult(
                payload.betId(),
                rewardOptional.isPresent(),
                rewardOptional.map(JackpotReward::getJackpotRewardAmount).orElse(BigDecimal.ZERO),
                jackpot.getCurrentBalance()
        );
    }
}
