package com.sporty.jackpot_service.service.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RewardChanceEvaluator {

    public boolean isWinningChance(BigDecimal chancePercentage) {
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < chancePercentage.doubleValue();
    }
}
