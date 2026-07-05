package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.UnitTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class RewardChanceEvaluatorTest {
    private final RewardChanceEvaluator chanceEvaluator = new RewardChanceEvaluator();

    @Test
    void isWinningChance_100PercentChance_True() {
        var chance = new BigDecimal("100");

        assertThat(chanceEvaluator.isWinningChance(chance)).isTrue();
    }

    @Test
    void isWinningChance_0PercentChance_False() {
        var chance = BigDecimal.ZERO;

        assertThat(chanceEvaluator.isWinningChance(chance)).isFalse();
    }
}
