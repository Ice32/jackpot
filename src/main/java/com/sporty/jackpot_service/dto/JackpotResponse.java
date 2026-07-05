package com.sporty.jackpot_service.dto;

import java.math.BigDecimal;

public record JackpotResponse(
        String jackpotId,
        String name,
        BigDecimal currentBalance
) {
}
