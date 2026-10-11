package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.CouponDto;
import com.bookverse.dto.CouponValidationResponse;
import com.bookverse.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(@RequestBody Map<String, Object> payload) {
        String code = (String) payload.get("code");
        BigDecimal subtotal = BigDecimal.ZERO;
        if (payload.get("subtotal") != null) {
            subtotal = new BigDecimal(payload.get("subtotal").toString());
        }

        CouponValidationResponse response = couponService.validateCoupon(code, subtotal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponDto>>> getCoupons() {
        return ResponseEntity.ok(ApiResponse.success(couponService.getAllCoupons()));
    }
}
