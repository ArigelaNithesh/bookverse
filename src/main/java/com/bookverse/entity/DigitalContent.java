package com.bookverse.entity;

import com.bookverse.enums.DigitalContentType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "digital_contents")
public class DigitalContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 50)
    private DigitalContentType contentType;

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "full_text", columnDefinition = "MEDIUMTEXT")
    private String fullText;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "rights_notice")
    private String rightsNotice;

    @Column(name = "gutenberg_id", length = 50)
    private String gutenbergId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public DigitalContent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public DigitalContentType getContentType() { return contentType; }
    public void setContentType(DigitalContentType contentType) { this.contentType = contentType; }

    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public String getRightsNotice() { return rightsNotice; }
    public void setRightsNotice(String rightsNotice) { this.rightsNotice = rightsNotice; }

    public String getGutenbergId() { return gutenbergId; }
    public void setGutenbergId(String gutenbergId) { this.gutenbergId = gutenbergId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
