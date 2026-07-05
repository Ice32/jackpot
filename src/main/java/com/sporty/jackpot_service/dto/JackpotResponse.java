package com.sporty.jackpot_service.dto;

import java.math.BigDecimal;

public record JackpotResponse(  // only used for the UI demo
        String jackpotId,
        String name,
        BigDecimal currentBalance
) {
}
