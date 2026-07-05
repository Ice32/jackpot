package com.sporty.jackpot_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "jackpot_contribution",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_jackpot_contribution_bet_id", columnNames = "betId")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JackpotContribution extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String betId;

    @Column(nullable = false, length = 50)
    private String userId;

    @Column(nullable = false, length = 50)
    private String jackpotId;

    @Column(precision = 18, scale = 4)
    private BigDecimal stakeAmount;

    @Column(precision = 18, scale = 4)
    private BigDecimal contributionAmount;

    @Column(precision = 18, scale = 4)
    private BigDecimal currentJackpotAmount;

    @Column(nullable = false)
    private boolean evaluated = false;

    public void markEvaluated() {
        this.evaluated = true;
    }
}
