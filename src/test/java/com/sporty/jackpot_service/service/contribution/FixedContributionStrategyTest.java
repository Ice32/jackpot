package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.UnitTest;
import com.sporty.jackpot_service.model.FixedContributionConfiguration;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class FixedContributionStrategyTest {

    private final FixedContributionStrategy strategy = new FixedContributionStrategy();

    @Test
    void calculateContribution() {
        BigDecimal rate = new BigDecimal("0.1");
        FixedContributionConfiguration configuration = new FixedContributionConfiguration(rate);
        BigDecimal stakeAmount = new BigDecimal("500");

        BigDecimal contribution = strategy.calculateContribution(stakeAmount, BigDecimal.ZERO, configuration);

        assertThat(contribution).isEqualByComparingTo(new BigDecimal("50"));
    }
}
