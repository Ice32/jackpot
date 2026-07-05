package com.sporty.jackpot_service.dto;

import jakarta.validation.constraints.NotBlank;

public record EvaluateBetRequest(
        @NotBlank String betId
) {
}
