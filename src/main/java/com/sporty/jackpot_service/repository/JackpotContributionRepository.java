package com.sporty.jackpot_service.repository;

import com.sporty.jackpot_service.model.JackpotContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotContributionRepository extends JpaRepository<JackpotContribution, Long> {

    Optional<JackpotContribution> findByJackpotId(String jackpotId);

    Optional<JackpotContribution> findByBetId(String betId);

    boolean existsByBetId(String betId);
}
