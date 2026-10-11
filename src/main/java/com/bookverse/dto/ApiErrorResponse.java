package com.bookverse.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApiErrorResponse {

    private boolean success = false;
    private int status;
    private String error;
    private String message;
    private List<String> validationErrors = new ArrayList<>();
    private LocalDateTime timestamp;

    public ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(int status, String error, String message) {
        this.success = false;
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<String> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(List<String> validationErrors) { this.validationErrors = validationErrors; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
