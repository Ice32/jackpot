package com.sporty.jackpot_service.repository;

import com.sporty.jackpot_service.model.JackpotContribution;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotContributionRepository extends JpaRepository<JackpotContribution, Long> {
    Optional<JackpotContribution> findByJackpotId(String jackpotId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)   // prevent concurrent requests evaluating
    @Query("SELECT jc FROM JackpotContribution jc WHERE jc.betId = :betId")
    Optional<JackpotContribution> findByBetIdWithWriteLock(String betId);

    boolean existsByBetId(String betId);
}
