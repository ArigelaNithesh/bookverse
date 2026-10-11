-- V2__create_catalog_tables.sql
-- Catalog: categories, authors, publishers, books, book_authors

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    parent_id BIGINT,
    display_order INT DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    biography TEXT,
    website VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE publishers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    website VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    slug VARCHAR(280) NOT NULL UNIQUE,
    description TEXT,
    isbn10 VARCHAR(10),
    isbn13 VARCHAR(13),
    publisher_id BIGINT,
    publication_date DATE,
    edition VARCHAR(50),
    language VARCHAR(50) DEFAULT 'English',
    page_count INT,
    cover_image_url VARCHAR(500),
    category_id BIGINT,
    list_price DECIMAL(10, 2) NOT NULL,
    selling_price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    average_rating DECIMAL(3, 2) DEFAULT 0.00,
    review_count INT DEFAULT 0,
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    bestseller BOOLEAN NOT NULL DEFAULT FALSE,
    digital_available BOOLEAN NOT NULL DEFAULT FALSE,
    digital_content_type VARCHAR(50),
    digital_content_reference VARCHAR(500),
    external_source VARCHAR(100),
    external_source_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_book_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE SET NULL,
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE book_authors (
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    author_order INT DEFAULT 0,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_ba_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT fk_ba_author FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE INDEX idx_books_slug ON books(slug);
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_isbn13 ON books(isbn13);
CREATE INDEX idx_books_category ON books(category_id);
CREATE INDEX idx_books_prices ON books(selling_price);
