package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.UnitTest;
import com.sporty.jackpot_service.model.VariableRewardConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@UnitTest
class VariableRewardStrategyTest {

    private static final BigDecimal BASE_CHANCE = new BigDecimal("5");
    private static final BigDecimal TIER_1_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal TIER_1_CHANCE = new BigDecimal("10");
    private static final BigDecimal TIER_2_THRESHOLD = new BigDecimal("5000");
    private static final BigDecimal TIER_2_CHANCE = new BigDecimal("25");
    private static final BigDecimal POOL_LIMIT = new BigDecimal("10000");

    @Mock
    private RewardChanceEvaluator rewardChanceEvaluator;

    @InjectMocks
    private VariableRewardStrategy strategy;

    @Test
    void evaluateWin_BelowTier1_UsesBaseChance() {
        when(rewardChanceEvaluator.isWinningChance(BASE_CHANCE)).thenReturn(true);

        var result = strategy.evaluateWin(TIER_1_THRESHOLD.subtract(BigDecimal.ONE), configuration());

        assertThat(result).isTrue();
        verify(rewardChanceEvaluator).isWinningChance(BASE_CHANCE);
    }

    @Test
    void evaluateWin_AboveTier1_UsesTier1Chance() {
        when(rewardChanceEvaluator.isWinningChance(TIER_1_CHANCE)).thenReturn(true);

        var result = strategy.evaluateWin(TIER_1_THRESHOLD.add(BigDecimal.ONE), configuration());

        assertThat(result).isTrue();
        verify(rewardChanceEvaluator).isWinningChance(TIER_1_CHANCE);
    }

    @Test
    void evaluateWin_AboveTier2_UsesTier2Chance() {
        when(rewardChanceEvaluator.isWinningChance(TIER_2_CHANCE)).thenReturn(true);

        var result = strategy.evaluateWin(TIER_2_THRESHOLD.add(BigDecimal.ONE), configuration());

        assertThat(result).isTrue();
        verify(rewardChanceEvaluator).isWinningChance(TIER_2_CHANCE);
    }

    @Test
    void evaluateWin_AtPoolLimit_WinsWithoutEvaluatingChance() {
        var strategy = new VariableRewardStrategy(rewardChanceEvaluator);

        var result = strategy.evaluateWin(POOL_LIMIT, configuration());

        assertThat(result).isTrue();
        verifyNoInteractions(rewardChanceEvaluator);
    }

    private static VariableRewardConfiguration configuration() {
        return new VariableRewardConfiguration(
                BASE_CHANCE,
                TIER_1_THRESHOLD,
                TIER_1_CHANCE,
                TIER_2_THRESHOLD,
                TIER_2_CHANCE,
                POOL_LIMIT
        );
    }
}
