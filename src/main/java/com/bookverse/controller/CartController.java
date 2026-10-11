package com.bookverse.controller;

import com.bookverse.dto.AddToCartRequest;
import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.CartDto;
import com.bookverse.dto.UpdateCartItemRequest;
import com.bookverse.entity.User;
import com.bookverse.service.AuthService;
import com.bookverse.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final AuthService authService;

    public CartController(CartService cartService, AuthService authService) {
        this.cartService = cartService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartDto(user)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        CartDto cart = cartService.addToCart(user, request);
        return ResponseEntity.ok(ApiResponse.success("Book added to cart", cart));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        CartDto cart = cartService.updateCartItem(user, id, request);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", cart));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto>> removeCartItem(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        CartDto cart = cartService.removeCartItem(user, id);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        User user = authService.getCurrentAuthenticatedUser();
        cartService.clearCart(user);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}
