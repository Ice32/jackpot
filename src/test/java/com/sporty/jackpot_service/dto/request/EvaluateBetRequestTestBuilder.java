package com.sporty.jackpot_service.dto.request;

import com.sporty.jackpot_service.dto.EvaluateBetRequest;
import com.sporty.jackpot_service.model.JackpotContribution;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public class EvaluateBetRequestTestBuilder {
    private String betId = UUID.randomUUID().toString();
    private String userId = UUID.randomUUID().toString();

    public EvaluateBetRequestTestBuilder(String betId) {
        this.betId = betId;
    }

    public EvaluateBetRequestTestBuilder(JackpotContribution jackpotContribution) {
        this.betId = jackpotContribution.getBetId();
    }

    public EvaluateBetRequest build() {
        return new EvaluateBetRequest(betId, userId);
    }

}
