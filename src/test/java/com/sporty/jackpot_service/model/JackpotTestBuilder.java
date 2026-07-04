package com.sporty.jackpot_service.model;

import java.math.BigDecimal;
import java.util.UUID;

public class JackpotTestBuilder {
    private Long id;
    private String jackpotId = UUID.randomUUID().toString();
    private String name = "a jackpot";
    private BigDecimal currentBalance = new BigDecimal("100");
    private BigDecimal baseAmount = new BigDecimal("10");
    private ContributionStrategyType contributionStrategy = ContributionStrategyType.FIXED;
    private RewardStrategyType rewardStrategy = RewardStrategyType.FIXED;

    public static JackpotTestBuilder fixedContributionRate() {
        return new JackpotTestBuilder()
                .contributionStrategy(ContributionStrategyType.FIXED);
    }

    public static JackpotTestBuilder variableContributionRate() {
        return new JackpotTestBuilder()
                .contributionStrategy(ContributionStrategyType.VARIABLE);
    }


    public JackpotTestBuilder contributionStrategy(ContributionStrategyType contributionStrategy) {
        this.contributionStrategy = contributionStrategy;
        return this;
    }

    public JackpotTestBuilder currentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
        return this;
    }

    public Jackpot build() {
        var jackpot = new Jackpot();
        jackpot.setId(id);
        jackpot.setJackpotId(jackpotId);
        jackpot.setName(name);
        jackpot.setCurrentBalance(currentBalance);
        jackpot.setBaseAmount(baseAmount);
        jackpot.setContributionStrategy(contributionStrategy);
        jackpot.setRewardStrategy(rewardStrategy);
        return jackpot;
    }
}
