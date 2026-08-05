package com.example.history_service.dto;

import java.time.LocalDateTime;

public class CallHistoryDto {

    private Long id;
    private String caller; // Masked for UI display
    private String callee; // Masked for UI display
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String duration;
    private String status;
    private String type;
    private String location;

    public CallHistoryDto() {
    }

    public CallHistoryDto(Long id, String caller, String callee, LocalDateTime startedAt, LocalDateTime endedAt, String duration, String status, String type, String location) {
        this.id = id;
        this.caller = caller;
        this.callee = callee;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.duration = duration;
        this.status = status;
        this.type = type;
        this.location = location;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaller() { return caller; }
    public void setCaller(String caller) { this.caller = caller; }

    public String getCallee() { return callee; }
    public void setCallee(String callee) { this.callee = callee; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
