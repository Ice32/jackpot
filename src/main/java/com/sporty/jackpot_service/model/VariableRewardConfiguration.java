package com.sporty.jackpot_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("VARIABLE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class VariableRewardConfiguration extends RewardConfiguration {

    @Column(precision = 8, scale = 4)
    private BigDecimal baseChance;

    @Column(precision = 18, scale = 4)
    private BigDecimal tier1Threshold;

    @Column(precision = 8, scale = 4)
    private BigDecimal tier1Chance;

    @Column(precision = 18, scale = 4)
    private BigDecimal tier2Threshold;

    @Column(precision = 8, scale = 4)
    private BigDecimal tier2Chance;

    @Column(precision = 18, scale = 4)
    private BigDecimal poolLimit;

    public VariableRewardConfiguration(
            BigDecimal baseChance,
            BigDecimal tier1Threshold,
            BigDecimal tier1Chance,
            BigDecimal tier2Threshold,
            BigDecimal tier2Chance,
            BigDecimal poolLimit
    ) {
        this.baseChance = baseChance;
        this.tier1Threshold = tier1Threshold;
        this.tier1Chance = tier1Chance;
        this.tier2Threshold = tier2Threshold;
        this.tier2Chance = tier2Chance;
        this.poolLimit = poolLimit;
    }

    @Override
    public RewardStrategyType getType() {
        return RewardStrategyType.VARIABLE;
    }
}
