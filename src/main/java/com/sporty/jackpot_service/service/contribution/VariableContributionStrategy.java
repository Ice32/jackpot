package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.model.ContributionStrategyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VariableContributionStrategy implements ContributionStrategy {

    @Value("${jackpot.strategies.contribution.variable.initial-rate:0.05}") // e.g., 5% initially
    private BigDecimal initialRate;

    @Value("${jackpot.strategies.contribution.variable.decay-step:1000.00}") // Rate drops per $1,000 in pool
    private BigDecimal decayStep;

    @Value("${jackpot.strategies.contribution.variable.decay-rate:0.005}") // Drops by 0.5% per step
    private BigDecimal decayRate;

    @Value("${jackpot.strategies.contribution.variable.floor-rate:0.01}") // Never drops below 1%
    private BigDecimal floorRate;

    @Override
    public BigDecimal calculateContribution(BigDecimal stakeAmount, BigDecimal currentPoolBalance) {
        if (stakeAmount == null || stakeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal safePoolBalance = (currentPoolBalance == null) ? BigDecimal.ZERO : currentPoolBalance;

        // 1. Calculate how many decay steps the pool has advanced
        // steps = currentPoolBalance / decayStep
        BigDecimal steps = safePoolBalance.divide(decayStep, 0, RoundingMode.DOWN);

        // 2. Calculate the total reduction: steps * decayRate
        BigDecimal totalDecay = steps.multiply(decayRate);

        // 3. Subtract decay from initial rate, but ensure it never falls below the floor rate
        BigDecimal calculatedRate = initialRate.subtract(totalDecay);
        BigDecimal finalRate = calculatedRate.max(floorRate);

        // 4. Calculate contribution as a percentage of the Bet Amount
        return stakeAmount.multiply(finalRate);
    }

    @Override
    public ContributionStrategyType getType() {
        return ContributionStrategyType.VARIABLE;
    }
}
