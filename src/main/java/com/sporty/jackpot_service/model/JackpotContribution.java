package com.sporty.jackpot_service.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "jackpot_contribution",
        indexes = {
                @Index(name = "idx_jackpot_contribution_jackpot_id", columnList = "jackpot_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_jackpot_contribution_bet_id", columnNames = "bet_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JackpotContribution extends BaseEntity {

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
            foreignKey = @ForeignKey(name = "fk_jackpot_contribution_jackpot")
    )
    private Jackpot jackpot;

    @Column(name = "stake_amount", precision = 18, scale = 4)
    private BigDecimal stakeAmount;

    @Column(name = "contribution_amount", precision = 18, scale = 4)
    private BigDecimal contributionAmount;

    @Column(name = "current_jackpot_amount", precision = 18, scale = 4)
    private BigDecimal currentJackpotAmount;

    @Column(nullable = false)
    private boolean evaluated = false;

    public JackpotContribution(
            String betId,
            String userId,
            String jackpotId,
            BigDecimal stakeAmount,
            BigDecimal contributionAmount,
            BigDecimal currentJackpotAmount,
            boolean evaluated
    ) {
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.stakeAmount = stakeAmount;
        this.contributionAmount = contributionAmount;
        this.currentJackpotAmount = currentJackpotAmount;
        this.evaluated = evaluated;
    }

    public void markEvaluated() {
        this.evaluated = true;
    }
}
