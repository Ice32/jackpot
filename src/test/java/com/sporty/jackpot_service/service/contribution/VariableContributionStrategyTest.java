package com.sporty.jackpot_service.service.contribution;

import com.sporty.jackpot_service.UnitTest;
import com.sporty.jackpot_service.model.VariableContributionConfiguration;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class VariableContributionStrategyTest {

    private static final BigDecimal INITIAL_RATE = new BigDecimal("0.1");
    private static final BigDecimal DECAY_STEP = new BigDecimal("1000");
    private static final BigDecimal DECAY_RATE = new BigDecimal("0.05");
    private static final BigDecimal FLOOR_RATE = new BigDecimal("0.05");

    private final VariableContributionStrategy strategy = new VariableContributionStrategy();

    @Test
    void calculateContribution_InitialRate() {
        var initialRate = new BigDecimal("0.1");
        var configuration = new VariableContributionConfiguration(initialRate, DECAY_STEP, DECAY_RATE, FLOOR_RATE);
        var stakeAmount = new BigDecimal("500");

        var contribution = strategy.calculateContribution(stakeAmount, BigDecimal.ZERO, configuration);

        assertThat(contribution).isEqualByComparingTo(new BigDecimal("50"));
    }

    @Test
    void calculateContribution_3DecaySteps() {
        var initialRate = new BigDecimal("0.5");
        var decayRate = new BigDecimal("0.1");
        var configuration = new VariableContributionConfiguration(initialRate, DECAY_STEP, decayRate, FLOOR_RATE);
        var stakeAmount = new BigDecimal("500");
        var decaySteps = new BigDecimal("3");
        var currentPoolBalance = DECAY_STEP.multiply(decaySteps);  // 3 decay steps
        var expectedContributionRate = initialRate.subtract(decayRate.multiply(decaySteps));

        var contribution = strategy.calculateContribution(stakeAmount, currentPoolBalance, configuration);

        assertThat(contribution).isEqualByComparingTo(stakeAmount.multiply(expectedContributionRate));
    }

    @Test
    void calculateContribution_FloorRateHit() {
        var decayStep = new BigDecimal("1");
        var decayRate = new BigDecimal("0.5");
        var florRate = new BigDecimal("0.1");
        var configuration = new VariableContributionConfiguration(INITIAL_RATE, decayStep, decayRate, florRate);
        var stakeAmount = new BigDecimal("500");
        var currentPoolBalance = new BigDecimal("9999999");

        var contribution = strategy.calculateContribution(stakeAmount, currentPoolBalance, configuration);

        assertThat(contribution).isEqualByComparingTo(stakeAmount.multiply(florRate));
    }
}
