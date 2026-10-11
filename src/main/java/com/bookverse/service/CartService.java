package com.bookverse.service;

import com.bookverse.dto.AddToCartRequest;
import com.bookverse.dto.CartDto;
import com.bookverse.dto.CartItemDto;
import com.bookverse.dto.UpdateCartItemRequest;
import com.bookverse.entity.Book;
import com.bookverse.entity.Cart;
import com.bookverse.entity.CartItem;
import com.bookverse.entity.User;
import com.bookverse.exception.BadRequestException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.CartItemRepository;
import com.bookverse.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional(readOnly = true)
    public CartDto getCartDto(User user) {
        Cart cart = getOrCreateCart(user);
        return mapToDto(cart);
    }

    @Transactional
    public CartDto addToCart(User user, AddToCartRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!book.isActive()) {
            throw new BadRequestException("This book is currently unavailable");
        }

        if (book.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + book.getStockQuantity() + ")");
        }

        Cart cart = getOrCreateCart(user);

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > book.getStockQuantity()) {
                throw new BadRequestException("Cannot add more of this item. Maximum available stock is " + book.getStockQuantity());
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, book, request.getQuantity());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return mapToDto(cart);
    }

    @Transactional
    public CartDto updateCartItem(User user, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to your cart");
        }

        Book book = item.getBook();
        if (request.getQuantity() > book.getStockQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + book.getStockQuantity() + ")");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return mapToDto(cart);
    }

    @Transactional
    public CartDto removeCartItem(User user, Long itemId) {
        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to your cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return mapToDto(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public CartDto mapToDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());

        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItem item : cart.getItems()) {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setId(item.getId());
            itemDto.setBookId(item.getBook().getId());
            itemDto.setBookTitle(item.getBook().getTitle());
            itemDto.setBookSlug(item.getBook().getSlug());
            itemDto.setCoverImageUrl(item.getBook().getCoverImageUrl());
            itemDto.setUnitPrice(item.getBook().getSellingPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setAvailableStock(item.getBook().getStockQuantity());

            BigDecimal lineTotal = item.getBook().getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            itemDto.setLineTotal(lineTotal);

            subtotal = subtotal.add(lineTotal);
            totalQuantity += item.getQuantity();

            itemDtos.add(itemDto);
        }

        dto.setItems(itemDtos);
        dto.setTotalItems(totalQuantity);
        dto.setSubtotal(subtotal);

        return dto;
    }
}
