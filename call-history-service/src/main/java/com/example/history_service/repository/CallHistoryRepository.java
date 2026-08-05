package com.example.history_service.repository;

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
}
