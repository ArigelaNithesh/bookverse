package com.bookverse.repository;

import com.bookverse.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Optional<Book> findBySlug(String slug);
    Optional<Book> findByIsbn13(String isbn13);
    Optional<Book> findByIsbn10(String isbn10);
    List<Book> findByFeaturedTrueAndActiveTrue();
    List<Book> findByBestsellerTrueAndActiveTrue();
    List<Book> findByCategoryIdAndActiveTrue(Long categoryId);
    Page<Book> findByActiveTrue(Pageable pageable);
    
    long countByActiveTrue();
    long countByStockQuantityLessThanEqual(int threshold);
    boolean existsBySlug(String slug);
    boolean existsByIsbn13(String isbn13);
    boolean existsByIsbn10(String isbn10);
    Optional<Book> findByExternalSourceAndExternalSourceId(String externalSource, String externalSourceId);
    Optional<Book> findFirstByTitleIgnoreCase(String title);
}
