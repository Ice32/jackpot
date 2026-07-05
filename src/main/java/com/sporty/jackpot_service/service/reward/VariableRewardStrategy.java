package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.model.RewardConfiguration;
import com.sporty.jackpot_service.model.RewardStrategyType;
import com.sporty.jackpot_service.model.VariableRewardConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class VariableRewardStrategy implements RewardStrategy {

    private final RewardChanceEvaluator rewardChanceEvaluator;

    @Override
    public boolean evaluateWin(BigDecimal currentPoolBalance, RewardConfiguration configuration) {
        if (currentPoolBalance == null || currentPoolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        VariableRewardConfiguration variableConfiguration = (VariableRewardConfiguration) configuration;

        // If the pool hits or exceeds the limit, win chance becomes 100%
        if (currentPoolBalance.compareTo(variableConfiguration.getPoolLimit()) >= 0) {
            return true;
        }

        BigDecimal winningChance = variableConfiguration.getBaseChance();

        if (currentPoolBalance.compareTo(variableConfiguration.getTier2Threshold()) > 0) {
            winningChance = variableConfiguration.getTier2Chance();
        } else if (currentPoolBalance.compareTo(variableConfiguration.getTier1Threshold()) > 0) {
            winningChance = variableConfiguration.getTier1Chance();
        }

        return rewardChanceEvaluator.isWinningChance(winningChance);
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.VARIABLE;
    }
}
