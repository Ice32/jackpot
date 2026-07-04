package com.sporty.jackpot_service.dto;

import java.math.BigDecimal;

public record EvaluationResult(
        String betId,
        boolean won,
        BigDecimal payoutAmount,
        BigDecimal remainingPoolBalance
) {

}
