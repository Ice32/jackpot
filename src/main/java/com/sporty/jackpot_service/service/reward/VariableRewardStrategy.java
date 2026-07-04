package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.config.VariableRewardProperties;
import com.sporty.jackpot_service.model.RewardStrategyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class VariableRewardStrategy implements RewardStrategy {

    private final VariableRewardProperties properties;

    @Override
    public boolean evaluateWin(BigDecimal currentPoolBalance) {
        if (currentPoolBalance == null || currentPoolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // 2. Evaluate dynamic odds scaling based on properties
        double winningChance = properties.baseChance();

        if (currentPoolBalance.compareTo(properties.tier2Threshold()) > 0) {
            winningChance = properties.tier2Chance();
        } else if (currentPoolBalance.compareTo(properties.tier1Threshold()) > 0) {
            winningChance = properties.tier1Chance();
        }

        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < winningChance;
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.VARIABLE;
    }
}
