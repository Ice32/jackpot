package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.model.ContributionConfiguration;
import com.sporty.jackpot_service.model.ContributionStrategyType;
import com.sporty.jackpot_service.model.VariableContributionConfiguration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VariableContributionStrategy implements ContributionStrategy {

    @Override
    public BigDecimal calculateContribution(
            BigDecimal stakeAmount,
            BigDecimal currentPoolBalance,
            ContributionConfiguration configuration
    ) {
        if (stakeAmount == null || stakeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        VariableContributionConfiguration variableConfiguration = (VariableContributionConfiguration) configuration;
        BigDecimal safePoolBalance = (currentPoolBalance == null) ? BigDecimal.ZERO : currentPoolBalance;

        // 1. Calculate how many decay steps the pool has advanced
        // steps = currentPoolBalance / decayStep
        BigDecimal steps = safePoolBalance.divide(variableConfiguration.getDecayStep(), 0, RoundingMode.DOWN);

        // 2. Calculate the total reduction: steps * decayRate
        BigDecimal totalDecay = steps.multiply(variableConfiguration.getDecayRate());

        // 3. Subtract decay from initial rate, but ensure it never falls below the floor rate
        BigDecimal calculatedRate = variableConfiguration.getInitialRate().subtract(totalDecay);
        BigDecimal finalRate = calculatedRate.max(variableConfiguration.getFloorRate());

        // 4. Calculate contribution as a percentage of the Bet Amount
        return stakeAmount.multiply(finalRate);
    }

    @Override
    public ContributionStrategyType getType() {
        return ContributionStrategyType.VARIABLE;
    }
}
