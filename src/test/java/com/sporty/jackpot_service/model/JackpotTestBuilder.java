package com.sporty.jackpot_service.model;

import java.math.BigDecimal;
import java.util.UUID;

public class JackpotTestBuilder {
    private Long id;
    private String jackpotId = UUID.randomUUID().toString();
    private String name = "a jackpot";
    private BigDecimal currentBalance = new BigDecimal("100");
    private BigDecimal baseAmount = new BigDecimal("10");
    private BigDecimal fixedContributionRate = new BigDecimal("0.10");
    private BigDecimal variableContributionInitialRate = new BigDecimal("0.30");
    private BigDecimal variableContributionDecayStep = new BigDecimal("1000.00");
    private BigDecimal variableContributionDecayRate = new BigDecimal("0.005");
    private BigDecimal variableContributionFloorRate = new BigDecimal("0.05");
    private BigDecimal fixedRewardWinChance = new BigDecimal("5.00");
    private BigDecimal variableRewardBaseChance = new BigDecimal("5.00");
    private BigDecimal variableRewardTier1Threshold = new BigDecimal("5000.00");
    private BigDecimal variableRewardTier1Chance = new BigDecimal("10.00");
    private BigDecimal variableRewardTier2Threshold = new BigDecimal("10000.00");
    private BigDecimal variableRewardTier2Chance = new BigDecimal("20.00");
    private BigDecimal variableRewardPoolLimit = new BigDecimal("25000.00");
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

    public static JackpotTestBuilder fixedRewardChance() {
        return new JackpotTestBuilder()
                .rewardStrategy(RewardStrategyType.FIXED);
    }

    public static JackpotTestBuilder variableRewardChance() {
        return new JackpotTestBuilder()
                .rewardStrategy(RewardStrategyType.VARIABLE);
    }

    public JackpotTestBuilder contributionStrategy(ContributionStrategyType contributionStrategy) {
        this.contributionStrategy = contributionStrategy;
        return this;
    }

    public JackpotTestBuilder rewardStrategy(RewardStrategyType rewardStrategy) {
        this.rewardStrategy = rewardStrategy;
        return this;
    }

    public JackpotTestBuilder currentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
        return this;
    }

    public JackpotTestBuilder baseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
        return this;
    }

    public JackpotTestBuilder fixedContributionRate(BigDecimal fixedContributionRate) {
        this.fixedContributionRate = fixedContributionRate;
        return this;
    }

    public JackpotTestBuilder variableContributionInitialRate(BigDecimal variableContributionInitialRate) {
        this.variableContributionInitialRate = variableContributionInitialRate;
        return this;
    }

    public JackpotTestBuilder variableContributionFloorRate(BigDecimal variableContributionFloorRate) {
        this.variableContributionFloorRate = variableContributionFloorRate;
        return this;
    }

    public JackpotTestBuilder fixedRewardWinChance(BigDecimal fixedRewardWinChance) {
        this.fixedRewardWinChance = fixedRewardWinChance;
        return this;
    }

    public JackpotTestBuilder variableRewardBaseChance(BigDecimal variableRewardBaseChance) {
        this.variableRewardBaseChance = variableRewardBaseChance;
        return this;
    }

    public JackpotTestBuilder variableRewardTier1Threshold(BigDecimal variableRewardTier1Threshold) {
        this.variableRewardTier1Threshold = variableRewardTier1Threshold;
        return this;
    }

    public JackpotTestBuilder variableRewardTier1Chance(BigDecimal variableRewardTier1Chance) {
        this.variableRewardTier1Chance = variableRewardTier1Chance;
        return this;
    }

    public JackpotTestBuilder variableRewardPoolLimit(BigDecimal variableRewardPoolLimit) {
        this.variableRewardPoolLimit = variableRewardPoolLimit;
        return this;
    }

    public Jackpot build() {
        var jackpot = new Jackpot();
        jackpot.setId(id);
        jackpot.setJackpotId(jackpotId);
        jackpot.setName(name);
        jackpot.setCurrentBalance(currentBalance);
        jackpot.setBaseAmount(baseAmount);
        jackpot.setContributionConfiguration(buildContributionConfiguration());
        jackpot.setRewardConfiguration(buildRewardConfiguration());
        return jackpot;
    }

    private ContributionConfiguration buildContributionConfiguration() {
        if (contributionStrategy == ContributionStrategyType.VARIABLE) {
            return new VariableContributionConfiguration(
                    variableContributionInitialRate,
                    variableContributionDecayStep,
                    variableContributionDecayRate,
                    variableContributionFloorRate
            );
        }

        return new FixedContributionConfiguration(fixedContributionRate);
    }

    private RewardConfiguration buildRewardConfiguration() {
        if (rewardStrategy == RewardStrategyType.VARIABLE) {
            return new VariableRewardConfiguration(
                    variableRewardBaseChance,
                    variableRewardTier1Threshold,
                    variableRewardTier1Chance,
                    variableRewardTier2Threshold,
                    variableRewardTier2Chance,
                    variableRewardPoolLimit
            );
        }

        return new FixedRewardConfiguration(fixedRewardWinChance);
    }
}
