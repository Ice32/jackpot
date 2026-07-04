package com.sporty.jackpot_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "jackpot_contribution")
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
}
