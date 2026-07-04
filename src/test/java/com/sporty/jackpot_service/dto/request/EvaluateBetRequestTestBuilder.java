package com.sporty.jackpot_service.dto.request;

import com.sporty.jackpot_service.dto.EvaluateBetRequest;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class EvaluateBetRequestTestBuilder {
    private String betId = UUID.randomUUID().toString();
    private String jackpotId = UUID.randomUUID().toString();
    private String userId = UUID.randomUUID().toString();

    public EvaluateBetRequestTestBuilder(String jackpotId) {
        this.jackpotId = jackpotId;
    }

    public EvaluateBetRequest build() {
        return new EvaluateBetRequest(betId, jackpotId, userId);
    }

}
