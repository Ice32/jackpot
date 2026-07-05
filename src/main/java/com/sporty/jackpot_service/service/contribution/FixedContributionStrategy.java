package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.model.ContributionConfiguration;
import com.sporty.jackpot_service.model.ContributionStrategyType;
import com.sporty.jackpot_service.model.FixedContributionConfiguration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedContributionStrategy implements ContributionStrategy {

    @Override
    public BigDecimal calculateContribution(
            BigDecimal stakeAmount,
            BigDecimal currentPoolBalance,
            ContributionConfiguration configuration
    ) {
        if (stakeAmount == null || stakeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        FixedContributionConfiguration fixedConfiguration = (FixedContributionConfiguration) configuration;
        return stakeAmount.multiply(fixedConfiguration.getRate());
    }

    @Override
    public ContributionStrategyType getType() {
        return ContributionStrategyType.FIXED;
    }
}
