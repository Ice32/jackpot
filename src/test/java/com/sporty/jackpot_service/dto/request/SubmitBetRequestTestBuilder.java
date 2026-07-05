package com.sporty.jackpot_service.dto.request;

import com.sporty.jackpot_service.dto.SubmitBetRequest;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
public class SubmitBetRequestTestBuilder {
    private String betId = UUID.randomUUID().toString();
    private String jackpotId = UUID.randomUUID().toString();
    private String userId = UUID.randomUUID().toString();
    private BigDecimal betAmount = new BigDecimal("10");

    public SubmitBetRequestTestBuilder(String jackpotId) {
        this.jackpotId = jackpotId;
    }

    public SubmitBetRequestTestBuilder amount(BigDecimal amount) {
        this.betAmount = amount;
        return this;
    }

    public SubmitBetRequest build() {
        return new SubmitBetRequest(betId, jackpotId, userId, betAmount);
    }
}
