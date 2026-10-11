package com.bookverse.service;

import com.bookverse.dto.AuthResponse;
import com.bookverse.dto.LoginRequest;
import com.bookverse.dto.RegisterRequest;
import com.bookverse.entity.Cart;
import com.bookverse.entity.Role;
import com.bookverse.entity.User;
import com.bookverse.entity.Wishlist;
import com.bookverse.enums.RoleType;
import com.bookverse.exception.ConflictException;
import com.bookverse.exception.UnauthorizedException;

import com.bookverse.repository.CartRepository;
import com.bookverse.repository.RoleRepository;
import com.bookverse.repository.UserRepository;
import com.bookverse.repository.WishlistRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       CartRepository cartRepository,
                       WishlistRepository wishlistRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.wishlistRepository = wishlistRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new ConflictException("An account with this email already exists");
        }

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_USER)));

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setActive(true);
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        // Initialize user cart and wishlist
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartRepository.save(cart);

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(savedUser);
        wishlistRepository.save(wishlist);

        Set<String> roleNames = savedUser.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new AuthResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), roleNames);
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String email = request.getEmail().toLowerCase().trim();
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new AuthResponse(user.getId(), user.getEmail(), user.getFullName(), roleNames);
    }

    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("Please sign in to proceed");
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User session not found"));
    }

    public AuthResponse getCurrentUserResponse() {
        User user = getCurrentAuthenticatedUser();
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
        return new AuthResponse(user.getId(), user.getEmail(), user.getFullName(), roleNames);
    }
}
