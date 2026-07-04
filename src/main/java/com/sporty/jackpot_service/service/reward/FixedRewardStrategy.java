package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.model.RewardStrategyType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class FixedRewardStrategy implements RewardStrategy {

    @Override
    public boolean evaluateWin(BigDecimal stakeAmount, BigDecimal currentPoolBalance) {
        if (currentPoolBalance == null || currentPoolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        // Uniform static probability odds: flat 0.5% chance to win on any bet
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < 0.5;
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.FIXED;
    }
}
