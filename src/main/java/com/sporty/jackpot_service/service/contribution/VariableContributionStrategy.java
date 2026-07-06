package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.model.ContributionConfiguration;
import com.sporty.jackpot_service.model.ContributionStrategyType;
import com.sporty.jackpot_service.model.VariableContributionConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Slf4j
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

        BigDecimal calculatedRate = calculateContributionRate(currentPoolBalance, (VariableContributionConfiguration) configuration);

        return stakeAmount.multiply(calculatedRate);
    }

    private static BigDecimal calculateContributionRate(BigDecimal currentPoolBalance, VariableContributionConfiguration variableConfiguration) {
        BigDecimal safePoolBalance = (currentPoolBalance == null) ? BigDecimal.ZERO : currentPoolBalance;

        // 1. Calculate how many decay steps the pool has advanced
        // steps = currentPoolBalance / decayStep
        BigDecimal steps = safePoolBalance.divide(variableConfiguration.getDecayStep(), 0, RoundingMode.DOWN);

        // 2. Calculate the total reduction: steps * decayRate
        BigDecimal totalDecay = steps.multiply(variableConfiguration.getDecayRate());

        // 3. Subtract decay from the initial rate
        BigDecimal calculatedRate = variableConfiguration.getInitialRate().subtract(totalDecay);

        // 4. Ensure it never falls below the floor rate
        log.debug("Variable contribution configuration: {}", variableConfiguration);
        log.debug("Decay steps: {}, total decay: {}, calculated rate: {}", steps, totalDecay, calculatedRate);
        return calculatedRate.max(variableConfiguration.getFloorRate());
    }

    @Override
    public ContributionStrategyType getType() {
        return ContributionStrategyType.VARIABLE;
    }
}
