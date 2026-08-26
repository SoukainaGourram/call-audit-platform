package com.example.history_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.history_service.entity.CallHistory;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {

    @Query("SELECT c FROM CallHistory c WHERE c.caller LIKE %:phone% OR c.callee LIKE %:phone%")
    List<CallHistory> searchByPhoneNumber(@Param("phone") String phone);

    @Query("SELECT c FROM CallHistory c WHERE c.caller LIKE :pattern OR c.callee LIKE :pattern")
    List<CallHistory> searchByPattern(@Param("pattern") String pattern);

    @Query("SELECT c FROM CallHistory c WHERE "
         + "(:phone IS NULL OR :phone = '' OR c.caller LIKE %:phone% OR c.callee LIKE %:phone% OR c.caller LIKE :pattern OR c.callee LIKE :pattern) AND "
         + "(:hasDateFilter = false OR (c.startedAt >= :startDate AND c.startedAt <= :endDate)) AND "
         + "(:type IS NULL OR :type = '' OR :type = 'ALL' OR "
         + " (:type = 'MISSED' AND (c.type = 'MISSED' OR c.status = 'MANQUE' OR c.status = 'Manqué')) OR "
         + " (:type = 'INCOMING' AND c.type = 'INCOMING') OR "
         + " (:type = 'OUTGOING' AND c.type = 'OUTGOING'))")
    List<CallHistory> searchCombined(
            @Param("phone") String phone,
            @Param("pattern") String pattern,
            @Param("hasDateFilter") boolean hasDateFilter,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") String type);
}
