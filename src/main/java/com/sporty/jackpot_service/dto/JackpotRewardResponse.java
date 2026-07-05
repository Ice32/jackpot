package com.sporty.jackpot_service.dto;

import java.math.BigDecimal;

public record JackpotRewardResponse(
        String betId,
        String userId,
        String jackpotId,
        BigDecimal jackpotRewardAmount
) {
}
