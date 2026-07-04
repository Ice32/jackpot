package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.model.ContributionStrategyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FixedContributionStrategy implements ContributionStrategy {

    @Value("${jackpot.strategies.contribution.fixed.contribution-rate}")
    private BigDecimal contributionRate;

    @Override
    public BigDecimal calculateContribution(BigDecimal stakeAmount, BigDecimal currentPoolBalance) {
        if (stakeAmount == null || stakeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return stakeAmount.multiply(contributionRate);
    }

    @Override
    public ContributionStrategyType getType() {
        return ContributionStrategyType.FIXED;
    }
}
