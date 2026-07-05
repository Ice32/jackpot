package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.UnitTest;
import com.sporty.jackpot_service.model.FixedRewardConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;

@UnitTest
class FixedRewardStrategyTest {

    @Mock
    private RewardChanceEvaluator rewardChanceEvaluator;

    @InjectMocks
    private FixedRewardStrategy strategy;

    @Test
    void evaluateWin_UsesConfiguredFixedChance() {
        var winChance = new BigDecimal("12.50");
        var configuration = new FixedRewardConfiguration(winChance);

        strategy.evaluateWin(new BigDecimal("100"), configuration);

        verify(rewardChanceEvaluator).isWinningChance(winChance);
    }
}
