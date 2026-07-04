package com.sporty.jackpot_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jackpot.strategies.reward.fixed")
public record FixedRewardProperties(double winChance) {
}
