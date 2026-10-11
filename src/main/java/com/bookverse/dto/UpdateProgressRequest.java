package com.bookverse.dto;

import java.math.BigDecimal;

public class UpdateProgressRequest {

    private Integer lastPositionScroll = 0;
    private BigDecimal completionPercentage = BigDecimal.ZERO;
    private String lastChapterTitle;

    public UpdateProgressRequest() {}

    public Integer getLastPositionScroll() { return lastPositionScroll; }
    public void setLastPositionScroll(Integer lastPositionScroll) { this.lastPositionScroll = lastPositionScroll; }

    public BigDecimal getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(BigDecimal completionPercentage) { this.completionPercentage = completionPercentage; }

    public String getLastChapterTitle() { return lastChapterTitle; }
    public void setLastChapterTitle(String lastChapterTitle) { this.lastChapterTitle = lastChapterTitle; }
}
