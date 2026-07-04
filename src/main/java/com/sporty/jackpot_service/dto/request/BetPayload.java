package com.sporty.jackpot_service.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BetPayload(
        @NotBlank String betId,
        @NotBlank String jackpotId,
        @NotBlank String userId,
        @NotNull @DecimalMin("0.01") BigDecimal betAmount
) {
}
