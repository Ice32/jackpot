package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.model.RewardConfiguration;
import com.sporty.jackpot_service.model.RewardStrategyType;
import com.sporty.jackpot_service.model.VariableRewardConfiguration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class VariableRewardStrategy implements RewardStrategy {

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

        double winningChance = variableConfiguration.getBaseChance().doubleValue();

        if (currentPoolBalance.compareTo(variableConfiguration.getTier2Threshold()) > 0) {
            winningChance = variableConfiguration.getTier2Chance().doubleValue();
        } else if (currentPoolBalance.compareTo(variableConfiguration.getTier1Threshold()) > 0) {
            winningChance = variableConfiguration.getTier1Chance().doubleValue();
        }

        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < winningChance;
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.VARIABLE;
    }
}
