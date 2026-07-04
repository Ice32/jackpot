package com.sporty.jackpot_service.model;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
public class JackpotContributionTestBuilder {
    private String betId = UUID.randomUUID().toString();
    private String userId = UUID.randomUUID().toString();
    private String jackpotId = UUID.randomUUID().toString();
    private BigDecimal stakeAmount = new BigDecimal("100");
    private BigDecimal contributionAmount = new BigDecimal("2");
    private BigDecimal currentJackpotAmount = new BigDecimal("1000");

   public JackpotContributionTestBuilder(String jackpotId) {
       this.jackpotId = jackpotId;
   }

    public JackpotContributionTestBuilder betId(String betId) {
        this.betId = betId;
        return this;
    }

    public JackpotContributionTestBuilder jackpotId(String jackpotId) {
        this.jackpotId = jackpotId;
        return this;
    }


    public JackpotContribution build() {
      return new JackpotContribution(
              betId,
              userId,
              jackpotId,
              stakeAmount,
              contributionAmount,
              currentJackpotAmount
      );
    }
}
