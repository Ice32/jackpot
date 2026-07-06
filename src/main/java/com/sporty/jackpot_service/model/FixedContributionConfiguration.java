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
@DiscriminatorValue("FIXED")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class FixedContributionConfiguration extends ContributionConfiguration {

    @Column(precision = 8, scale = 4)
    private BigDecimal rate;

    public FixedContributionConfiguration(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public ContributionStrategyType getType() {
        return ContributionStrategyType.FIXED;
    }
}
