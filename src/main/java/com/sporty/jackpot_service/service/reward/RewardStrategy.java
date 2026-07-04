package com.sporty.jackpot_service.service.reward;

import com.sporty.jackpot_service.model.RewardStrategyType;
import java.math.BigDecimal;

public interface RewardStrategy {
    /**
     * Determines whether the current bet wins the jackpot reward.
     * @return true if it's a win, false otherwise.
     */
    boolean evaluateWin(BigDecimal currentPoolBalance);

    /**
     * Matches the strategy implementation to the DB Enum configuration type.
     */
    RewardStrategyType getType();
}
