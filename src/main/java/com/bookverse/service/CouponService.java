package com.bookverse.service;

import com.bookverse.dto.CouponDto;
import com.bookverse.dto.CouponValidationResponse;
import com.bookverse.entity.Coupon;
import com.bookverse.exception.ConflictException;
import com.bookverse.repository.CouponRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponValidationResponse validateCoupon(String code, BigDecimal subtotal) {
        if (code == null || code.trim().isEmpty()) {
            return new CouponValidationResponse(false, "", "Coupon code is empty", BigDecimal.ZERO);
        }

        String normalizedCode = code.trim().toUpperCase();
        Coupon coupon = couponRepository.findByCodeAndActiveTrue(normalizedCode).orElse(null);

        if (coupon == null) {
            return new CouponValidationResponse(false, normalizedCode, "Invalid or expired coupon code", BigDecimal.ZERO);
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            return new CouponValidationResponse(false, normalizedCode, "Coupon is not active yet", BigDecimal.ZERO);
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return new CouponValidationResponse(false, normalizedCode, "Coupon has expired", BigDecimal.ZERO);
        }
        if (coupon.getUsageLimit() != null && coupon.getTimesUsed() >= coupon.getUsageLimit()) {
            return new CouponValidationResponse(false, normalizedCode, "Coupon usage limit reached", BigDecimal.ZERO);
        }

        if (subtotal != null && coupon.getMinimumOrderAmount() != null
                && subtotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            return new CouponValidationResponse(false, normalizedCode,
                    "Order minimum of ₹" + coupon.getMinimumOrderAmount() + " required for this coupon", BigDecimal.ZERO);
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountPercentage() != null && coupon.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            if (subtotal != null) {
                discount = subtotal.multiply(coupon.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
        } else if (coupon.getFlatDiscountAmount() != null) {
            discount = coupon.getFlatDiscountAmount();
            if (subtotal != null && discount.compareTo(subtotal) > 0) {
                discount = subtotal;
            }
        }

        return new CouponValidationResponse(true, normalizedCode, "Coupon applied successfully!", discount);
    }

    @Transactional
    public void incrementCouponUsage(String code) {
        if (code != null && !code.trim().isEmpty()) {
            couponRepository.findByCode(code.trim().toUpperCase()).ifPresent(c -> {
                c.setTimesUsed(c.getTimesUsed() + 1);
                couponRepository.save(c);
            });
        }
    }

    public List<CouponDto> getAllCoupons() {
        return couponRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public CouponDto createCoupon(CouponDto dto) {
        String code = dto.getCode().trim().toUpperCase();
        if (couponRepository.findByCode(code).isPresent()) {
            throw new ConflictException("Coupon code already exists");
        }

        Coupon c = new Coupon();
        c.setCode(code);
        c.setDiscountPercentage(dto.getDiscountPercentage());
        c.setFlatDiscountAmount(dto.getFlatDiscountAmount());
        c.setMinimumOrderAmount(dto.getMinimumOrderAmount() != null ? dto.getMinimumOrderAmount() : BigDecimal.ZERO);
        c.setValidFrom(dto.getValidFrom());
        c.setValidUntil(dto.getValidUntil());
        c.setActive(dto.isActive());

        return mapToDto(couponRepository.save(c));
    }

    private CouponDto mapToDto(Coupon coupon) {
        CouponDto dto = new CouponDto();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDiscountPercentage(coupon.getDiscountPercentage());
        dto.setFlatDiscountAmount(coupon.getFlatDiscountAmount());
        dto.setMinimumOrderAmount(coupon.getMinimumOrderAmount());
        dto.setValidFrom(coupon.getValidFrom());
        dto.setValidUntil(coupon.getValidUntil());
        dto.setActive(coupon.isActive());
        return dto;
    }
}
