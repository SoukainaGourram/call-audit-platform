package com.example.search_history.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "search_logs")
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String searchedNumber;

    @Column(nullable = false)
    private LocalDateTime searchTimestamp;

    private int resultCount;

    public SearchLog() {
    }

    public SearchLog(Long id, String username, String searchedNumber, LocalDateTime searchTimestamp, int resultCount) {
        this.id = id;
        this.username = username;
        this.searchedNumber = searchedNumber;
        this.searchTimestamp = searchTimestamp;
        this.resultCount = resultCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSearchedNumber() { return searchedNumber; }
    public void setSearchedNumber(String searchedNumber) { this.searchedNumber = searchedNumber; }

    public LocalDateTime getSearchTimestamp() { return searchTimestamp; }
    public void setSearchTimestamp(LocalDateTime searchTimestamp) { this.searchTimestamp = searchTimestamp; }

    public int getResultCount() { return resultCount; }
    public void setResultCount(int resultCount) { this.resultCount = resultCount; }
}
