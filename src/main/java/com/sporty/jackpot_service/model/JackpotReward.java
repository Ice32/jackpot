package com.sporty.jackpot_service.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "jackpot_reward",
        indexes = {
                @Index(name = "idx_jackpot_reward_jackpot_id", columnList = "jackpot_id"),
                @Index(name = "idx_jackpot_reward_created_at", columnList = "created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_jackpot_reward_bet_id", columnNames = "bet_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JackpotReward extends BaseEntity {

    @Column(name = "bet_id", nullable = false, length = 50)
    private String betId;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "jackpot_id", nullable = false, length = 50)
    private String jackpotId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "jackpot_id",
            referencedColumnName = "jackpot_id",
            insertable = false,
            updatable = false,
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_jackpot_reward_jackpot")
    )
    private Jackpot jackpot;

    @Column(name = "jackpot_reward_amount", nullable = false, precision = 18, scale = 4)
    private BigDecimal jackpotRewardAmount;

    public JackpotReward(String betId, String userId, String jackpotId, BigDecimal jackpotRewardAmount) {
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.jackpotRewardAmount = jackpotRewardAmount;
    }
}
