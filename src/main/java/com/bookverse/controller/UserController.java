package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.ChangePasswordRequest;
import com.bookverse.dto.UserProfileResponse;
import com.bookverse.entity.User;
import com.bookverse.service.AuthService;
import com.bookverse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        User user = authService.getCurrentAuthenticatedUser();
        UserProfileResponse profile = userService.getProfile(user);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@RequestBody Map<String, String> body) {
        User user = authService.getCurrentAuthenticatedUser();
        String fullName = body.get("fullName");
        String phoneNumber = body.get("phoneNumber");
        UserProfileResponse profile = userService.updateProfile(user, fullName, phoneNumber);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        userService.changePassword(user, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
