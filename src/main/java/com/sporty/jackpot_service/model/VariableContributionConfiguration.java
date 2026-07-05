package com.sporty.jackpot_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("VARIABLE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VariableContributionConfiguration extends ContributionConfiguration {

    @Column(precision = 8, scale = 4)
    private BigDecimal initialRate;

    @Column(precision = 18, scale = 4)
    private BigDecimal decayStep;

    @Column(precision = 8, scale = 4)
    private BigDecimal decayRate;

    @Column(precision = 8, scale = 4)
    private BigDecimal floorRate;

    public VariableContributionConfiguration(
            BigDecimal initialRate,
            BigDecimal decayStep,
            BigDecimal decayRate,
            BigDecimal floorRate
    ) {
        this.initialRate = initialRate;
        this.decayStep = decayStep;
        this.decayRate = decayRate;
        this.floorRate = floorRate;
    }

    @Override
    public ContributionStrategyType getType() {
        return ContributionStrategyType.VARIABLE;
    }
}
