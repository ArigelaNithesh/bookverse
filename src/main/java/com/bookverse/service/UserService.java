package com.bookverse.service;

import com.bookverse.dto.ChangePasswordRequest;
import com.bookverse.dto.UserProfileResponse;
import com.bookverse.entity.User;
import com.bookverse.exception.BadRequestException;
import com.bookverse.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfileResponse getProfile(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
        response.setRoles(roles);

        return response;
    }

    @Transactional
    public UserProfileResponse updateProfile(User user, String fullName, String phoneNumber) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber.trim());
        }
        User updated = userRepository.save(user);
        return getProfile(updated);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (request.getNewPassword().length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters long");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
