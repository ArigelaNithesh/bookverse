package com.bookverse.service;

import com.bookverse.dto.AdminDashboardStatsDto;
import com.bookverse.dto.BookSummaryDto;
import com.bookverse.dto.OrderDto;
import com.bookverse.dto.UserProfileResponse;
import com.bookverse.entity.Category;
import com.bookverse.entity.User;

import com.bookverse.enums.OrderStatus;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.CategoryRepository;
import com.bookverse.repository.OrderRepository;
import com.bookverse.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final BookService bookService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminService(BookRepository bookRepository,
                        UserRepository userRepository,
                        OrderRepository orderRepository,
                        CategoryRepository categoryRepository,
                        BookService bookService,
                        OrderService orderService,
                        UserService userService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.bookService = bookService;
        this.orderService = orderService;
        this.userService = userService;
    }

    public AdminDashboardStatsDto getDashboardStats() {
        AdminDashboardStatsDto stats = new AdminDashboardStatsDto();

        stats.setTotalBooks(bookRepository.count());
        stats.setTotalActiveBooks(bookRepository.countByActiveTrue());
        stats.setTotalCustomers(userRepository.count());
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByOrderStatus(OrderStatus.PLACED));
        stats.setCompletedOrders(orderRepository.countByOrderStatus(OrderStatus.DELIVERED));

        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        stats.setTotalRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        long lowStockCount = bookRepository.countByStockQuantityLessThanEqual(5);
        stats.setLowStockCount(lowStockCount);

        // Low stock books (stock <= 5)
        List<BookSummaryDto> lowStockBooks = bookRepository.findAll().stream()
                .filter(b -> b.getStockQuantity() != null && b.getStockQuantity() <= 5)
                .limit(10)
                .map(bookService::mapToSummaryDto)
                .collect(Collectors.toList());
        stats.setLowStockBooks(lowStockBooks);

        // Recent orders
        List<OrderDto> recentOrders = orderRepository.findRecentOrders(PageRequest.of(0, 5)).stream()
                .map(orderService::mapToDto)
                .collect(Collectors.toList());
        stats.setRecentOrders(recentOrders);

        // Category distribution
        Map<String, Long> categoryDistribution = new HashMap<>();
        List<Category> categories = categoryRepository.findAll();
        for (Category cat : categories) {
            long count = bookRepository.findAll().stream()
                    .filter(b -> b.getCategory() != null && b.getCategory().getId().equals(cat.getId()))
                    .count();
            if (count > 0) {
                categoryDistribution.put(cat.getName(), count);
            }
        }
        stats.setCategoryDistribution(categoryDistribution);

        return stats;
    }

    public List<UserProfileResponse> getAllCustomers() {
        return userRepository.findAll().stream()
                .map(userService::getProfile)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfileResponse toggleUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setActive(active);
        User saved = userRepository.save(user);
        return userService.getProfile(saved);
    }
}
