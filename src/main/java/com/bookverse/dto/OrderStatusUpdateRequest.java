package com.bookverse.dto;

import com.bookverse.enums.OrderStatus;
import com.bookverse.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    public OrderStatusUpdateRequest() {}

    public OrderStatusUpdateRequest(OrderStatus orderStatus, PaymentStatus paymentStatus) {
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
    }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}
