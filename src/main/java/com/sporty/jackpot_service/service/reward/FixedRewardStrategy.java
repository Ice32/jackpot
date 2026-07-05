package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.model.FixedRewardConfiguration;
import com.sporty.jackpot_service.model.RewardConfiguration;
import com.sporty.jackpot_service.model.RewardStrategyType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class FixedRewardStrategy implements RewardStrategy {

    @Override
    public boolean evaluateWin(BigDecimal currentPoolBalance, RewardConfiguration configuration) {
        if (currentPoolBalance == null || currentPoolBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        FixedRewardConfiguration fixedConfiguration = (FixedRewardConfiguration) configuration;
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < fixedConfiguration.getWinChance().doubleValue();
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.FIXED;
    }
}
