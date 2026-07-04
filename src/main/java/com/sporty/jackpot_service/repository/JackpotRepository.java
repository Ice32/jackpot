package com.sporty.jackpot_service.repository;

import com.sporty.jackpot_service.model.Jackpot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotRepository extends JpaRepository<Jackpot, Long> {
    /**
     * Retrieves a Jackpot configuration and locks the matching database row immediately.
     * Maps down to a native "SELECT ... FOR UPDATE" SQL statement.
     *
     * Concurrent Kafka listener threads attempting to process contributions for the
     * SAME jackpotId will cleanly block here until the holding transaction commits.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Jackpot j WHERE j.jackpotId = :jackpotId")
    Optional<Jackpot> findByJackpotIdWithWriteLock(@Param("jackpotId") String jackpotId);

}
