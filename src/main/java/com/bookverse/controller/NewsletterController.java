package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.service.NewsletterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        newsletterService.subscribe(email);
        return ResponseEntity.ok(ApiResponse.success("Successfully subscribed to newsletter!", null));
    }
}
