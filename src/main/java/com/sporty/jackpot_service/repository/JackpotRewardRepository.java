package com.sporty.jackpot_service.repository;

import com.sporty.jackpot_service.model.JackpotReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JackpotRewardRepository extends JpaRepository<JackpotReward, Long> {

    boolean existsByBetId(String betId);
}
