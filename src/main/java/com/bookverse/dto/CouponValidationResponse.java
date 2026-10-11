package com.bookverse.dto;

import java.math.BigDecimal;

public class CouponValidationResponse {

    private boolean valid;
    private String code;
    private String message;
    private BigDecimal discountAmount = BigDecimal.ZERO;

    public CouponValidationResponse() {}

    public CouponValidationResponse(boolean valid, String code, String message, BigDecimal discountAmount) {
        this.valid = valid;
        this.code = code;
        this.message = message;
        this.discountAmount = discountAmount;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
}
