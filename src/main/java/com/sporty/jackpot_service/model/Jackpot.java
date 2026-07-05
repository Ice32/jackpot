package com.sporty.jackpot_service.model;

import com.sporty.jackpot_service.dto.SubmitBetRequest;
import com.sporty.jackpot_service.service.JackpotStrategyFactory;
import com.sporty.jackpot_service.service.contribution.ContributionStrategy;
import com.sporty.jackpot_service.service.reward.RewardStrategy;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(
        name = "jackpot",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_jackpot_jackpot_id", columnNames = "jackpot_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
@Slf4j
public class Jackpot extends BaseEntity {

    @Column(name = "jackpot_id", nullable = false, length = 50)
    private String jackpotId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal currentBalance;

    // The baseline seed betAmount the pool resets to after someone wins
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal baseAmount;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(
            name = "contribution_configuration_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_jackpot_contribution_configuration")
    )
    private ContributionConfiguration contributionConfiguration;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(
            name = "reward_configuration_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_jackpot_reward_configuration")
    )
    private RewardConfiguration rewardConfiguration;

    // Concurrency guard: protects against race conditions when updating the balance
    @Version
    private Long version;

    public JackpotContribution contribute(SubmitBetRequest payload, JackpotStrategyFactory strategyFactory) {
        BigDecimal calculatedContribution = calculateContribution(payload.betAmount(), strategyFactory);

        log.debug("Applying {} allocation logic. Base: {}, Result: {}",
                getContributionStrategy(), payload.betAmount(), calculatedContribution);

        this.currentBalance = this.currentBalance.add(calculatedContribution);

        return new JackpotContribution(
                payload.betId(),
                payload.userId(),
                payload.jackpotId(),
                payload.betAmount(),
                calculatedContribution,
                getCurrentBalance(),
                false
        );
    }

    private BigDecimal calculateContribution(BigDecimal betAmount, JackpotStrategyFactory strategyFactory) {
        ContributionStrategy strategy = strategyFactory.getContributionStrategy(getContributionStrategy());

        return strategy.calculateContribution(
                betAmount,
                getCurrentBalance(),
                getContributionConfiguration()
        );
    }

    public Optional<JackpotReward> evaluate(JackpotContribution contribution, JackpotStrategyFactory strategyFactory) {
        RewardStrategy rewardStrategy = strategyFactory.getRewardStrategy(getRewardStrategy());

        boolean won = rewardStrategy.evaluateWin(getCurrentBalance(), getRewardConfiguration());

        Optional<JackpotReward> rewardOptional = Optional.empty();
        if (won) {
            rewardOptional = Optional.of(
                    new JackpotReward(
                            contribution.getBetId(),
                            contribution.getUserId(),
                            contribution.getJackpotId(),
                            getCurrentBalance() // Winner takes the current pool balance
                    ));

            // Reset jackpot back to seed/base amount
            resetToBase();
        }

        contribution.markEvaluated();

        return rewardOptional;
    }

    public void resetToBase() {
        this.currentBalance = this.baseAmount;
    }

    public ContributionStrategyType getContributionStrategy() {
        return contributionConfiguration.getType();
    }

    public RewardStrategyType getRewardStrategy() {
        return rewardConfiguration.getType();
    }
}
