package com.bookverse.dto;

import com.bookverse.enums.ImportStatus;
import java.time.LocalDateTime;

public class MetadataImportJobDto {

    private Long id;
    private String sourceName;
    private String searchQuery;
    private Integer requestedCount;
    private Integer importedCount;
    private Integer skippedCount;
    private Integer failureCount;
    private ImportStatus status;
    private String errorSummary;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    public MetadataImportJobDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public Integer getRequestedCount() { return requestedCount; }
    public void setRequestedCount(Integer requestedCount) { this.requestedCount = requestedCount; }

    public Integer getImportedCount() { return importedCount; }
    public void setImportedCount(Integer importedCount) { this.importedCount = importedCount; }

    public Integer getSkippedCount() { return skippedCount; }
    public void setSkippedCount(Integer skippedCount) { this.skippedCount = skippedCount; }

    public Integer getFailureCount() { return failureCount; }
    public void setFailureCount(Integer failureCount) { this.failureCount = failureCount; }

    public ImportStatus getStatus() { return status; }
    public void setStatus(ImportStatus status) { this.status = status; }

    public String getErrorSummary() { return errorSummary; }
    public void setErrorSummary(String errorSummary) { this.errorSummary = errorSummary; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }
}
