package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.model.ContributionStrategyType;
import com.sporty.jackpot_service.model.ContributionConfiguration;
import java.math.BigDecimal;

public interface ContributionStrategy {
    /**
     * Calculates the monetary betAmount to contribute to the jackpot pool.
     */
    BigDecimal calculateContribution(BigDecimal stakeAmount, BigDecimal currentPoolBalance, ContributionConfiguration configuration);

    /**
     * Matches the strategy implementation to the DB Enum configuration type.
     */
    ContributionStrategyType getType();
}
