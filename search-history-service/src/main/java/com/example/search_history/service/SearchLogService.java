package com.example.search_history.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.search_history.entity.SearchLog;
import com.example.search_history.repository.SearchLogRepository;

@Service
public class SearchLogService {

    private final SearchLogRepository repository;

    public SearchLogService(SearchLogRepository repository) {
        this.repository = repository;
    }

    public SearchLog recordSearch(String username, String searchedNumber, int resultCount) {
        SearchLog log = new SearchLog(
                null,
                username != null && !username.trim().isEmpty() ? username : "anonyme",
                searchedNumber,
                LocalDateTime.now(),
                resultCount
        );
        return repository.save(log);
    }

    public List<SearchLog> findAll() {
        return repository.findAllByOrderBySearchTimestampDesc();
    }

    public List<SearchLog> findByUsername(String username) {
        return repository.findByUsernameOrderBySearchTimestampDesc(username);
    }
}
