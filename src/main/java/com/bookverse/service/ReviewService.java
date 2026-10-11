package com.bookverse.service;

import com.bookverse.dto.CreateReviewRequest;
import com.bookverse.dto.ReviewDto;
import com.bookverse.entity.Book;
import com.bookverse.entity.Review;
import com.bookverse.entity.User;
import com.bookverse.exception.ConflictException;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.OrderItemRepository;
import com.bookverse.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final OrderItemRepository orderItemRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         OrderItemRepository orderItemRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<ReviewDto> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBookIdAndApprovedTrueOrderByCreatedAtDesc(bookId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto createReview(User user, Long bookId, CreateReviewRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (reviewRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new ConflictException("You have already submitted a review for this book");
        }

        boolean verified = orderItemRepository.hasUserPurchasedBook(user.getId(), bookId);

        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setHeadline(request.getHeadline());
        review.setReviewText(request.getReviewText());
        review.setVerifiedPurchase(verified);
        review.setApproved(true);

        Review saved = reviewRepository.save(review);

        // Recalculate book average rating & review count
        BigDecimal avg = reviewRepository.calculateAverageRating(bookId);
        Integer count = reviewRepository.countApprovedReviews(bookId);

        if (avg != null) {
            book.setAverageRating(avg.setScale(2, RoundingMode.HALF_UP));
        } else {
            book.setAverageRating(BigDecimal.valueOf(request.getRating()));
        }
        book.setReviewCount(count != null ? count : 1);
        bookRepository.save(book);

        return mapToDto(saved);
    }

    public ReviewDto mapToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setReviewerName(review.getUser().getFullName());
        dto.setBookId(review.getBook().getId());
        dto.setBookTitle(review.getBook().getTitle());
        dto.setRating(review.getRating());
        dto.setHeadline(review.getHeadline());
        dto.setReviewText(review.getReviewText());
        dto.setVerifiedPurchase(review.isVerifiedPurchase());
        dto.setApproved(review.isApproved());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
