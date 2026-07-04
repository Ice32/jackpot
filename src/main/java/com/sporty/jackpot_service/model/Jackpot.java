package com.sporty.jackpot_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "jackpot", indexes = {
        @Index(name = "unique_jackpot_jackpot_id", columnList = "jackpotId", unique = true)
})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Getter
public class Jackpot extends BaseEntity {

    @Column(nullable = false)
    private String jackpotId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal currentBalance;

    // The baseline seed betAmount the pool resets to after someone wins
    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal baseAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContributionStrategyType contributionStrategy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RewardStrategyType rewardStrategy;

    // Concurrency guard: protects against race conditions when updating the balance
    @Version
    private Long version;

    public void incrementBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        this.currentBalance = this.currentBalance.add(amount);
    }

    public void resetToBase() {
        this.currentBalance = this.baseAmount;
    }
}
