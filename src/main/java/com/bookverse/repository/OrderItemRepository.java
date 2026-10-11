package com.bookverse.repository;

import com.bookverse.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.order.user.id = :userId AND oi.book.id = :bookId AND oi.order.orderStatus <> 'CANCELLED'")
    boolean hasUserPurchasedBook(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("bookId") Long bookId);
}

