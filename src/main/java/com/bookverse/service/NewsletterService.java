package com.bookverse.service;

import com.bookverse.entity.NewsletterSubscription;
import com.bookverse.repository.NewsletterSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterService {

    private final NewsletterSubscriptionRepository newsletterRepository;

    public NewsletterService(NewsletterSubscriptionRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    @Transactional
    public void subscribe(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!newsletterRepository.existsByEmail(normalizedEmail)) {
            NewsletterSubscription sub = new NewsletterSubscription();
            sub.setEmail(normalizedEmail);
            newsletterRepository.save(sub);
        }
    }
}
