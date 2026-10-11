package com.bookverse.dto;

import com.bookverse.enums.DigitalContentType;

public class DigitalContentDto {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private DigitalContentType contentType;
    private String contentUrl;
    private String fullText;
    private Long fileSizeBytes;
    private String rightsNotice;
    private String gutenbergId;

    public DigitalContentDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

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
}
