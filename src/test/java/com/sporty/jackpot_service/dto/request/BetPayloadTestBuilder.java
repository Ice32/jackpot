package com.sporty.jackpot_service.dto.request;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
public class BetPayloadTestBuilder {
    private String betId = UUID.randomUUID().toString();
    private String jackpotId = UUID.randomUUID().toString();
    private String userId = UUID.randomUUID().toString();
    private BigDecimal betAmount = new BigDecimal("10");

    public BetPayloadTestBuilder(String jackpotId) {
        this.jackpotId = jackpotId;
    }

    public BetPayloadTestBuilder amount(BigDecimal amount) {
        this.betAmount = amount;
        return this;
    }

    public BetPayload build() {
        return new BetPayload(betId, jackpotId, userId, betAmount);
    }

}
