package com.sporty.jackpot_service.service.reward;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Centralize chance evaluation logic and make reward strategies unit-testable
 */
@Component
public class RewardChanceEvaluator {

    public boolean isWinningChance(BigDecimal chancePercentage) {
        return ThreadLocalRandom.current().nextDouble(0.0, 100.0) < chancePercentage.doubleValue();
    }
}
