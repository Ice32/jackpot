package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.model.ContributionStrategyType;
import java.math.BigDecimal;

public interface ContributionStrategy {
    /**
     * Calculates the monetary betAmount to contribute to the jackpot pool.
     */
    BigDecimal calculateContribution(BigDecimal stakeAmount, BigDecimal currentPoolBalance);

    /**
     * Matches the strategy implementation to the DB Enum configuration type.
     */
    ContributionStrategyType getType();
}
