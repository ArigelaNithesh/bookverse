package com.bookverse.specification;

import com.bookverse.entity.Author;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<Book> filterBooks(
            String keyword,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minRating,
            Integer year,
            String language,
            Boolean digitalAvailable,
            Boolean activeOnly
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (activeOnly == null || activeOnly) {
                predicates.add(cb.equal(root.get("active"), true));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
                Join<Book, Author> authorJoin = root.join("authors", JoinType.LEFT);
                Join<Book, Category> categoryJoin = root.join("category", JoinType.LEFT);

                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate isbn13Match = cb.like(cb.lower(root.get("isbn13")), searchPattern);
                Predicate isbn10Match = cb.like(cb.lower(root.get("isbn10")), searchPattern);
                Predicate authorMatch = cb.like(cb.lower(authorJoin.get("name")), searchPattern);
                Predicate categoryMatch = cb.like(cb.lower(categoryJoin.get("name")), searchPattern);

                predicates.add(cb.or(titleMatch, isbn13Match, isbn10Match, authorMatch, categoryMatch));
                query.distinct(true);
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sellingPrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("sellingPrice"), maxPrice));
            }

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
            }

            if (year != null) {
                predicates.add(cb.equal(cb.function("YEAR", Integer.class, root.get("publicationDate")), year));
            }

            if (language != null && !language.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("language")), language.trim().toLowerCase()));
            }

            if (digitalAvailable != null && digitalAvailable) {
                predicates.add(cb.equal(root.get("digitalAvailable"), true));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
