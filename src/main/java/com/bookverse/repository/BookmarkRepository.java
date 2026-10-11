package com.bookverse.repository;

import com.bookverse.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUserIdAndBookIdOrderByPositionIndexAsc(Long userId, Long bookId);
    void deleteByIdAndUserId(Long id, Long userId);
}
