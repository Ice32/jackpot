package com.sporty.jackpot_service.repository;

import com.sporty.jackpot_service.model.JackpotContribution;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotContributionRepository extends JpaRepository<JackpotContribution, Long> {

    Optional<JackpotContribution> findByJackpotId(String jackpotId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM JackpotContribution c WHERE c.betId = :betId")
    Optional<JackpotContribution> findByBetIdWithWriteLock(@Param("betId") String betId);

    @Query("SELECT c.jackpotId FROM JackpotContribution c WHERE c.betId = :betId")
    Optional<String> findJackpotIdByBetId(@Param("betId") String betId);

    boolean existsByBetId(String betId);
}
