package com.bookverse.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardStatsDto {

    private long totalBooks;
    private long totalActiveBooks;
    private long totalCustomers;
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private long lowStockCount;

    private List<BookSummaryDto> lowStockBooks = new ArrayList<>();
    private List<OrderDto> recentOrders = new ArrayList<>();
    private Map<String, Long> categoryDistribution = new HashMap<>();

    public AdminDashboardStatsDto() {}

    public long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }

    public long getTotalActiveBooks() { return totalActiveBooks; }
    public void setTotalActiveBooks(long totalActiveBooks) { this.totalActiveBooks = totalActiveBooks; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

    public long getCompletedOrders() { return completedOrders; }
    public void setCompletedOrders(long completedOrders) { this.completedOrders = completedOrders; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public List<BookSummaryDto> getLowStockBooks() { return lowStockBooks; }
    public void setLowStockBooks(List<BookSummaryDto> lowStockBooks) { this.lowStockBooks = lowStockBooks; }

    public List<OrderDto> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderDto> recentOrders) { this.recentOrders = recentOrders; }

    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }
}
