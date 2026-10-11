package com.bookverse.repository;

import com.bookverse.entity.Order;
import com.bookverse.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    long countByOrderStatus(OrderStatus status);

    @Query("SELECT SUM(o.grandTotal) FROM Order o WHERE o.orderStatus IN ('CONFIRMED', 'PACKED', 'SHIPPED', 'DELIVERED')")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);
}
