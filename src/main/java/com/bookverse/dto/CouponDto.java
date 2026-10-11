package com.bookverse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDto {

    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal flatDiscountAmount;
    private BigDecimal minimumOrderAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;

    public CouponDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getFlatDiscountAmount() { return flatDiscountAmount; }
    public void setFlatDiscountAmount(BigDecimal flatDiscountAmount) { this.flatDiscountAmount = flatDiscountAmount; }

    public BigDecimal getMinimumOrderAmount() { return minimumOrderAmount; }
    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) { this.minimumOrderAmount = minimumOrderAmount; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
