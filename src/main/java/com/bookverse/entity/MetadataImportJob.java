package com.bookverse.entity;

import com.bookverse.enums.ImportStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metadata_import_jobs")
public class MetadataImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_name", nullable = false, length = 100)
    private String sourceName;

    @Column(name = "search_query")
    private String searchQuery;

    @Column(name = "requested_count")
    private Integer requestedCount = 0;

    @Column(name = "imported_count")
    private Integer importedCount = 0;

    @Column(name = "skipped_count")
    private Integer skippedCount = 0;

    @Column(name = "failure_count")
    private Integer failureCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ImportStatus status = ImportStatus.PENDING;

    @Column(name = "error_summary", columnDefinition = "TEXT")
    private String errorSummary;

    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
    }

    public MetadataImportJob() {}

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
