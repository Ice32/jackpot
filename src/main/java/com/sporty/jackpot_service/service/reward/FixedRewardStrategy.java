package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.config.FixedRewardProperties;
import com.sporty.jackpot_service.model.RewardStrategyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class FixedRewardStrategy implements RewardStrategy {

    private final FixedRewardProperties properties;

    @Override
    public boolean evaluateWin(BigDecimal currentPoolBalance) {
        if (currentPoolBalance == null || currentPoolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // Dynamic flat uniform probability odds bound by config properties
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < properties.winChance();
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.FIXED;
    }
}