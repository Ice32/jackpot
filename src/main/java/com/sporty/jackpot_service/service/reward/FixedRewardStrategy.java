package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.model.FixedRewardConfiguration;
import com.sporty.jackpot_service.model.RewardConfiguration;
import com.sporty.jackpot_service.model.RewardStrategyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class FixedRewardStrategy implements RewardStrategy {

    private final RewardChanceEvaluator rewardChanceEvaluator;

    @Override
    public boolean evaluateWin(BigDecimal currentPoolBalance, RewardConfiguration configuration) {
        if (currentPoolBalance == null || currentPoolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        FixedRewardConfiguration fixedConfiguration = (FixedRewardConfiguration) configuration;
        log.debug("Using {}", fixedConfiguration);
        return rewardChanceEvaluator.isWinningChance(fixedConfiguration.getWinChance());
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.FIXED;
    }
}
