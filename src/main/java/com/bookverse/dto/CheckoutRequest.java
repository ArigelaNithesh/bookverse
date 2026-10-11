package com.bookverse.dto;

import com.bookverse.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    private Long addressId;
    private AddressDto newAddress;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod = PaymentMethod.CASH_ON_DELIVERY;

    private String couponCode;
    private String notes;

    public CheckoutRequest() {}

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public AddressDto getNewAddress() { return newAddress; }
    public void setNewAddress(AddressDto newAddress) { this.newAddress = newAddress; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
