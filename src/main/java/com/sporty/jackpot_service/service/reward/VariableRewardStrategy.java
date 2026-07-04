package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.model.RewardStrategyType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class VariableRewardStrategy implements RewardStrategy {

    @Override
    public boolean evaluateWin(BigDecimal stakeAmount, BigDecimal currentPoolBalance) {
        if (currentPoolBalance == null || currentPoolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // Shifting odds model: your winning probability scales up as the jackpot pool balance grows larger
        double winningChance = 0.1; // Base 0.1% chance
        
        if (currentPoolBalance.compareTo(new BigDecimal("10000.00")) > 0) {
            winningChance = 2.0; // Dynamic bump to 2% probability once pool hits critical sizes
        } else if (currentPoolBalance.compareTo(new BigDecimal("5000.00")) > 0) {
            winningChance = 1.0; // 1% probability
        }

        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < winningChance;
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.VARIABLE;
    }
}
