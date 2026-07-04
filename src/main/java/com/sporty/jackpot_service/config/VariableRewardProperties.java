package com.sporty.jackpot_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "jackpot.strategies.reward.variable")
public record VariableRewardProperties(
        double baseChance,
        BigDecimal tier1Threshold,
        double tier1Chance,
        BigDecimal tier2Threshold,
        double tier2Chance
) {

}
