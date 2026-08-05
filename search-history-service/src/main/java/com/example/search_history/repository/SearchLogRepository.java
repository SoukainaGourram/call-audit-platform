package com.example.search_history.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.search_history.entity.SearchLog;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
    List<SearchLog> findByUsernameOrderBySearchTimestampDesc(String username);
    List<SearchLog> findAllByOrderBySearchTimestampDesc();
}
