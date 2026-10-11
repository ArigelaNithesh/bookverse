package com.bookverse.service;

import com.bookverse.dto.AddToCartRequest;
import com.bookverse.dto.BookSummaryDto;
import com.bookverse.dto.WishlistDto;
import com.bookverse.entity.Book;
import com.bookverse.entity.User;
import com.bookverse.entity.Wishlist;
import com.bookverse.entity.WishlistItem;
import com.bookverse.exception.ConflictException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.WishlistItemRepository;
import com.bookverse.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final CartService cartService;

    public WishlistService(WishlistRepository wishlistRepository,
                           WishlistItemRepository wishlistItemRepository,
                           BookRepository bookRepository,
                           BookService bookService,
                           CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.cartService = cartService;
    }

    @Transactional
    public Wishlist getOrCreateWishlist(User user) {
        return wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wishlist w = new Wishlist(user);
                    return wishlistRepository.save(w);
                });
    }

    @Transactional(readOnly = true)
    public WishlistDto getWishlistDto(User user) {
        Wishlist wishlist = getOrCreateWishlist(user);
        return mapToDto(wishlist);
    }

    @Transactional
    public WishlistDto addToWishlist(User user, Long bookId) {
        Wishlist wishlist = getOrCreateWishlist(user);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (wishlistItemRepository.existsByWishlistIdAndBookId(wishlist.getId(), book.getId())) {
            throw new ConflictException("Book is already in your wishlist");
        }

        WishlistItem item = new WishlistItem(wishlist, book);
        wishlist.getItems().add(item);
        wishlistItemRepository.save(item);

        return mapToDto(wishlist);
    }

    @Transactional
    public WishlistDto removeFromWishlist(User user, Long itemId) {
        Wishlist wishlist = getOrCreateWishlist(user);
        WishlistItem item = wishlistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        if (!item.getWishlist().getId().equals(wishlist.getId())) {
            throw new ResourceNotFoundException("Item does not belong to your wishlist");
        }

        wishlist.getItems().remove(item);
        wishlistItemRepository.delete(item);

        return mapToDto(wishlist);
    }

    @Transactional
    public void moveToCart(User user, Long itemId) {
        Wishlist wishlist = getOrCreateWishlist(user);
        WishlistItem item = wishlistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        if (!item.getWishlist().getId().equals(wishlist.getId())) {
            throw new ResourceNotFoundException("Item does not belong to your wishlist");
        }

        // Add to cart
        cartService.addToCart(user, new AddToCartRequest(item.getBook().getId(), 1));

        // Remove from wishlist
        wishlist.getItems().remove(item);
        wishlistItemRepository.delete(item);
    }

    private WishlistDto mapToDto(Wishlist wishlist) {
        WishlistDto dto = new WishlistDto();
        dto.setId(wishlist.getId());

        List<BookSummaryDto> bookDtos = wishlist.getItems().stream()
                .map(item -> bookService.mapToSummaryDto(item.getBook()))
                .collect(Collectors.toList());

        dto.setBooks(bookDtos);
        return dto;
    }
}
