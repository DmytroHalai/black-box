package org.example.web.model;

import java.time.LocalDateTime;

public class CheckResult {
    private LocalDateTime timestamp;
    private boolean isCorrect;

    public CheckResult(LocalDateTime timestamp, boolean isCorrect) {
        this.timestamp = timestamp;
        this.isCorrect = isCorrect;
    }

    public CheckResult() {
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "timestamp=" + timestamp +
                ", isCorrect=" + isCorrect +
                '}';
    }
}
