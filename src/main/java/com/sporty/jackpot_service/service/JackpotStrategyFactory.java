package com.sporty.jackpot_service.service;

import com.sporty.jackpot_service.model.ContributionStrategyType;
import com.sporty.jackpot_service.model.RewardStrategyType;
import com.sporty.jackpot_service.service.contribution.ContributionStrategy;
import com.sporty.jackpot_service.service.reward.RewardStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JackpotStrategyFactory {

    private final Map<ContributionStrategyType, ContributionStrategy> contributionStrategies;
    private final Map<RewardStrategyType, RewardStrategy> rewardStrategies;

    public JackpotStrategyFactory(
            List<ContributionStrategy> contributionList,
            List<RewardStrategy> rewardList) {

        this.contributionStrategies = contributionList.stream()
                .collect(Collectors.toMap(ContributionStrategy::getType, Function.identity()));

        this.rewardStrategies = rewardList.stream()
                .collect(Collectors.toMap(RewardStrategy::getType, Function.identity()));
    }

    public ContributionStrategy getContributionStrategy(ContributionStrategyType type) {
        return Optional.ofNullable(contributionStrategies.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported contribution strategy: " + type));
    }

    public RewardStrategy getRewardStrategy(RewardStrategyType type) {
        return Optional.ofNullable(rewardStrategies.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported reward strategy: " + type));
    }
}