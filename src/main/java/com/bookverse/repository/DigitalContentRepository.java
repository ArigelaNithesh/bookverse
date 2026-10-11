package com.bookverse.repository;

import com.bookverse.entity.DigitalContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DigitalContentRepository extends JpaRepository<DigitalContent, Long> {
    Optional<DigitalContent> findByBookId(Long bookId);
}
