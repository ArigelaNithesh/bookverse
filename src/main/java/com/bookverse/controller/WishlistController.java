package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.WishlistDto;
import com.bookverse.entity.User;
import com.bookverse.service.AuthService;
import com.bookverse.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final AuthService authService;

    public WishlistController(WishlistService wishlistService, AuthService authService) {
        this.wishlistService = wishlistService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WishlistDto>> getWishlist() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlistDto(user)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<WishlistDto>> addToWishlist(@RequestBody Map<String, Long> payload) {
        User user = authService.getCurrentAuthenticatedUser();
        Long bookId = payload.get("bookId");
        if (bookId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("bookId is required"));
        }
        WishlistDto wishlist = wishlistService.addToWishlist(user, bookId);
        return ResponseEntity.ok(ApiResponse.success("Book added to wishlist", wishlist));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<WishlistDto>> removeFromWishlist(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        WishlistDto wishlist = wishlistService.removeFromWishlist(user, id);
        return ResponseEntity.ok(ApiResponse.success("Item removed from wishlist", wishlist));
    }

    @PostMapping("/items/{id}/move-to-cart")
    public ResponseEntity<ApiResponse<Void>> moveToCart(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        wishlistService.moveToCart(user, id);
        return ResponseEntity.ok(ApiResponse.success("Item moved to cart", null));
    }
}
