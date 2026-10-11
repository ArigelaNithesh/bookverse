package com.bookverse.repository;

import com.bookverse.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookIdAndApprovedTrueOrderByCreatedAtDesc(Long bookId);
    Optional<Review> findByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId AND r.approved = true")
    BigDecimal calculateAverageRating(Long bookId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.id = :bookId AND r.approved = true")
    Integer countApprovedReviews(Long bookId);
}
