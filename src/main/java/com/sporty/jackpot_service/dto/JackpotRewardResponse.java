package com.sporty.jackpot_service.dto;

import java.math.BigDecimal;

public record JackpotRewardResponse(  // only used for the UI demo
        String betId,
        String userId,
        String jackpotId,
        BigDecimal jackpotRewardAmount
) {
}
