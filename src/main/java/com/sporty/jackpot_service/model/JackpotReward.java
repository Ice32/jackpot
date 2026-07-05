package com.sporty.jackpot_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "jackpot_reward", indexes = {
        @Index(name = "idx_jackpot_reward_bet_id", columnList = "betId"),
        @Index(name = "idx_jackpot_reward_jackpot_id", columnList = "jackpotId")
})
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JackpotReward extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String betId;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 50)
    private String jackpotId;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal jackpotRewardAmount;
}
