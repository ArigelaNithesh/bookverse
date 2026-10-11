package com.bookverse.controller;

import com.bookverse.dto.AddressDto;
import com.bookverse.dto.ApiResponse;
import com.bookverse.entity.User;
import com.bookverse.service.AddressService;
import com.bookverse.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AuthService authService;

    public AddressController(AddressService addressService, AuthService authService) {
        this.addressService = addressService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDto>>> getAddresses() {
        User user = authService.getCurrentAuthenticatedUser();
        List<AddressDto> addresses = addressService.getUserAddresses(user);
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(@Valid @RequestBody AddressDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        AddressDto saved = addressService.addAddress(user, dto);
        return new ResponseEntity<>(ApiResponse.success("Address added successfully", saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDto>> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDto dto) {
        User user = authService.getCurrentAuthenticatedUser();
        AddressDto updated = addressService.updateAddress(user, id, dto);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        addressService.deleteAddress(user, id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }
}
