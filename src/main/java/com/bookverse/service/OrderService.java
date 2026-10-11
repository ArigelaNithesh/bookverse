package com.bookverse.service;

import com.bookverse.dto.*;
import com.bookverse.entity.*;
import com.bookverse.enums.OrderStatus;
import com.bookverse.enums.PaymentMethod;
import com.bookverse.enums.PaymentStatus;
import com.bookverse.exception.BadRequestException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.exception.UnauthorizedException;
import com.bookverse.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final AddressRepository addressRepository;
    private final CouponService couponService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PaymentRepository paymentRepository,
                        CartRepository cartRepository,
                        BookRepository bookRepository,
                        AddressRepository addressRepository,
                        CouponService couponService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.addressRepository = addressRepository;
        this.couponService = couponService;
    }

    @Transactional
    public OrderDto checkout(User user, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Shopping cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Your shopping cart is empty");
        }

        // Validate or resolve shipping address
        String recipientName;
        String phoneNumber;
        String street;
        String city;
        String state;
        String postalCode;
        String country;

        if (request.getAddressId() != null) {
            Address address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
            if (!address.getUser().getId().equals(user.getId())) {
                throw new BadRequestException("Address does not belong to you");
            }
            recipientName = address.getRecipientName();
            phoneNumber = address.getPhoneNumber();
            street = address.getStreetAddress();
            city = address.getCity();
            state = address.getState();
            postalCode = address.getPostalCode();
            country = address.getCountry();
        } else if (request.getNewAddress() != null) {
            AddressDto aDto = request.getNewAddress();
            if (aDto.getRecipientName() == null || aDto.getStreetAddress() == null || aDto.getCity() == null) {
                throw new BadRequestException("Incomplete address details");
            }
            Address newAddress = new Address();
            newAddress.setUser(user);
            newAddress.setRecipientName(aDto.getRecipientName());
            newAddress.setPhoneNumber(aDto.getPhoneNumber() != null ? aDto.getPhoneNumber() : user.getPhoneNumber());
            newAddress.setStreetAddress(aDto.getStreetAddress());
            newAddress.setCity(aDto.getCity());
            newAddress.setState(aDto.getState());
            newAddress.setPostalCode(aDto.getPostalCode());
            newAddress.setCountry(aDto.getCountry() != null ? aDto.getCountry() : "India");
            addressRepository.save(newAddress);

            recipientName = newAddress.getRecipientName();
            phoneNumber = newAddress.getPhoneNumber();
            street = newAddress.getStreetAddress();
            city = newAddress.getCity();
            state = newAddress.getState();
            postalCode = newAddress.getPostalCode();
            country = newAddress.getCountry();
        } else {
            throw new BadRequestException("Please select or enter a delivery address");
        }

        // Validate stock and compute subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            if (!book.isActive()) {
                throw new BadRequestException("Book '" + book.getTitle() + "' is no longer available");
            }
            if (book.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for '" + book.getTitle() + "'. Available: " + book.getStockQuantity());
            }
            BigDecimal lineTotal = book.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }

        // Process coupon discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCoupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            CouponValidationResponse couponRes = couponService.validateCoupon(request.getCouponCode(), subtotal);
            if (couponRes.isValid()) {
                discountAmount = couponRes.getDiscountAmount();
                appliedCoupon = couponRes.getCode();
                couponService.incrementCouponUsage(appliedCoupon);
            }
        }

        BigDecimal shippingFee = BigDecimal.ZERO; // Free shipping
        BigDecimal grandTotal = subtotal.subtract(discountAmount).add(shippingFee);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        // Generate unique order number
        String orderNumber = "BV-" + System.currentTimeMillis() + "-" + (1000 + new Random().nextInt(9000));

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);
        order.setGrandTotal(grandTotal);
        order.setRecipientName(recipientName);
        order.setPhoneNumber(phoneNumber);
        order.setShippingStreet(street);
        order.setShippingCity(city);
        order.setShippingState(state);
        order.setShippingPostalCode(postalCode);
        order.setShippingCountry(country);
        order.setCouponCode(appliedCoupon);
        order.setNotes(request.getNotes());

        Order savedOrder = orderRepository.save(order);

        // Deduct inventory and save order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
            bookRepository.save(book);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setBook(book);
            orderItem.setBookTitleSnapshot(book.getTitle());
            orderItem.setIsbnSnapshot(book.getIsbn13() != null ? book.getIsbn13() : book.getIsbn10());
            orderItem.setUnitPrice(book.getSellingPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setLineTotal(book.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItems.add(orderItemRepository.save(orderItem));
        }
        savedOrder.setItems(orderItems);

        // Create Payment record
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CASH_ON_DELIVERY);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(grandTotal);
        payment.setTransactionReference("TXN-" + System.currentTimeMillis());
        paymentRepository.save(payment);
        savedOrder.setPayment(payment);

        // Clear user cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(User user) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied: Order does not belong to you");
        }

        return mapToDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"));
        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied: Order does not belong to you");
        }

        if (order.getOrderStatus() != OrderStatus.PLACED && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("Order cannot be cancelled in status: " + order.getOrderStatus());
        }

        // Restore stock
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getBook() != null) {
                    Book book = item.getBook();
                    book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
                    bookRepository.save(book);
                }
            }
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        if (order.getPayment() != null) {
            order.getPayment().setPaymentStatus(PaymentStatus.REFUNDED);
        }

        Order saved = orderRepository.save(order);
        return mapToDto(saved);
    }

    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapToDto);
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(request.getOrderStatus());
        if (request.getPaymentStatus() != null && order.getPayment() != null) {
            order.getPayment().setPaymentStatus(request.getPaymentStatus());
            if (request.getPaymentStatus() == PaymentStatus.PAID && order.getPayment().getPaidAt() == null) {
                order.getPayment().setPaidAt(LocalDateTime.now());
            }
        }

        return mapToDto(orderRepository.save(order));
    }

    public OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setCustomerEmail(order.getUser().getEmail());
        dto.setCustomerName(order.getUser().getFullName());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingFee(order.getShippingFee());
        dto.setGrandTotal(order.getGrandTotal());
        dto.setRecipientName(order.getRecipientName());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setShippingStreet(order.getShippingStreet());
        dto.setShippingCity(order.getShippingCity());
        dto.setShippingState(order.getShippingState());
        dto.setShippingPostalCode(order.getShippingPostalCode());
        dto.setShippingCountry(order.getShippingCountry());
        dto.setCouponCode(order.getCouponCode());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (order.getPayment() != null) {
            dto.setPaymentMethod(order.getPayment().getPaymentMethod());
            dto.setPaymentStatus(order.getPayment().getPaymentStatus());
        }

        if (order.getItems() != null) {
            List<OrderItemDto> itemDtos = order.getItems().stream().map(i -> {
                OrderItemDto oi = new OrderItemDto();
                oi.setId(i.getId());
                if (i.getBook() != null) {
                    oi.setBookId(i.getBook().getId());
                    oi.setCoverImageUrl(i.getBook().getCoverImageUrl());
                }
                oi.setBookTitle(i.getBookTitleSnapshot());
                oi.setIsbn(i.getIsbnSnapshot());
                oi.setUnitPrice(i.getUnitPrice());
                oi.setQuantity(i.getQuantity());
                oi.setLineTotal(i.getLineTotal());
                return oi;
            }).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }
}
