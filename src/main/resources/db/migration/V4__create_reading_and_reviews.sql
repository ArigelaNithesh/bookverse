-- V4__create_reading_and_reviews.sql
-- Digital Reading, Bookmarks, Reviews, and Metadata Import Jobs

CREATE TABLE digital_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL UNIQUE,
    content_type VARCHAR(50) NOT NULL, -- e.g., PUBLIC_DOMAIN_TEXT, PDF, EPUB, EXTERNAL_LINK
    content_url VARCHAR(500),
    full_text MEDIUMTEXT,
    file_size_bytes BIGINT,
    rights_notice VARCHAR(255),
    gutenberg_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dc_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE TABLE reading_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    last_position_scroll INT DEFAULT 0,
    completion_percentage DECIMAL(5, 2) DEFAULT 0.00,
    last_chapter_title VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_book_reading UNIQUE (user_id, book_id)
);

CREATE TABLE bookmarks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    position_index INT NOT NULL,
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bm_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    headline VARCHAR(150),
    review_text TEXT,
    verified_purchase BOOLEAN DEFAULT FALSE,
    approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rev_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_book_review UNIQUE (user_id, book_id)
);

CREATE TABLE metadata_import_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_name VARCHAR(100) NOT NULL, -- OPEN_LIBRARY, PROJECT_GUTENBERG
    search_query VARCHAR(255),
    requested_count INT DEFAULT 0,
    imported_count INT DEFAULT 0,
    skipped_count INT DEFAULT 0,
    failure_count INT DEFAULT 0,
    status VARCHAR(50) NOT NULL, -- PENDING, IN_PROGRESS, COMPLETED, FAILED
    error_summary TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP NULL
);
