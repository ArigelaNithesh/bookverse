package com.bookverse.service;

import com.bookverse.dto.AuthorDto;
import com.bookverse.dto.BookDto;
import com.bookverse.dto.BookSummaryDto;
import com.bookverse.entity.Author;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import com.bookverse.entity.Publisher;
import com.bookverse.exception.ResourceNotFoundException;

import com.bookverse.repository.AuthorRepository;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.CategoryRepository;
import com.bookverse.repository.PublisherRepository;
import com.bookverse.specification.BookSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BookService(BookRepository bookRepository,
                       CategoryRepository categoryRepository,
                       AuthorRepository authorRepository,
                       PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    public Page<BookSummaryDto> searchBooks(String keyword,
                                            Long categoryId,
                                            BigDecimal minPrice,
                                            BigDecimal maxPrice,
                                            BigDecimal minRating,
                                            Integer year,
                                            String language,
                                            Boolean digitalAvailable,
                                            Pageable pageable) {
        Specification<Book> spec = BookSpecification.filterBooks(
                keyword, categoryId, minPrice, maxPrice, minRating, year, language, digitalAvailable, true
        );
        return bookRepository.findAll(spec, pageable).map(this::mapToSummaryDto);
    }

    public Page<BookSummaryDto> searchAdminBooks(String keyword,
                                                 Long categoryId,
                                                 Pageable pageable) {
        Specification<Book> spec = BookSpecification.filterBooks(
                keyword, categoryId, null, null, null, null, null, null, false
        );
        return bookRepository.findAll(spec, pageable).map(this::mapToSummaryDto);
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToDto(book);
    }

    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    public BookDto getBookBySlug(String slug) {
        Book book = bookRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with slug: " + slug));
        return mapToDto(book);
    }

    public List<BookSummaryDto> getFeaturedBooks() {
        return bookRepository.findByFeaturedTrueAndActiveTrue().stream()
                .limit(8)
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    public List<BookSummaryDto> getBestsellers() {
        return bookRepository.findByBestsellerTrueAndActiveTrue().stream()
                .limit(8)
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    public Page<BookSummaryDto> getBooksByCategory(Long categoryId, Pageable pageable) {
        Specification<Book> spec = BookSpecification.filterBooks(
                null, categoryId, null, null, null, null, null, null, true
        );
        return bookRepository.findAll(spec, pageable).map(this::mapToSummaryDto);
    }

    @Transactional
    public BookDto createBook(BookDto dto) {
        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = dto.getTitle().toLowerCase().trim().replaceAll("[^a-z0-9-]", "-");
        }
        if (bookRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setSubtitle(dto.getSubtitle());
        book.setSlug(slug);
        book.setDescription(dto.getDescription());
        book.setIsbn10(dto.getIsbn10());
        book.setIsbn13(dto.getIsbn13());
        book.setPublicationDate(dto.getPublicationDate());
        book.setEdition(dto.getEdition());
        book.setLanguage(dto.getLanguage() != null ? dto.getLanguage() : "English");
        book.setPageCount(dto.getPageCount());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setListPrice(dto.getListPrice());
        book.setSellingPrice(dto.getSellingPrice());
        book.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 10);
        book.setFeatured(dto.isFeatured());
        book.setActive(dto.isActive());
        book.setBestseller(dto.isBestseller());
        book.setDigitalAvailable(dto.isDigitalAvailable());
        book.setDigitalContentType(dto.getDigitalContentType());
        book.setDigitalContentReference(dto.getDigitalContentReference());
        book.setExternalSource(dto.getExternalSource());
        book.setExternalSourceId(dto.getExternalSourceId());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            book.setCategory(category);
        }

        if (dto.getPublisherName() != null && !dto.getPublisherName().trim().isEmpty()) {
            Publisher publisher = publisherRepository.findByName(dto.getPublisherName().trim())
                    .orElseGet(() -> {
                        Publisher p = new Publisher();
                        p.setName(dto.getPublisherName().trim());
                        return publisherRepository.save(p);
                    });
            book.setPublisher(publisher);
        }

        if (dto.getAuthors() != null && !dto.getAuthors().isEmpty()) {
            Set<Author> authorSet = new HashSet<>();
            for (AuthorDto aDto : dto.getAuthors()) {
                if (aDto.getName() != null && !aDto.getName().trim().isEmpty()) {
                    Author author = authorRepository.findByName(aDto.getName().trim())
                            .orElseGet(() -> {
                                Author a = new Author();
                                a.setName(aDto.getName().trim());
                                a.setBiography(aDto.getBiography());
                                return authorRepository.save(a);
                            });
                    authorSet.add(author);
                }
            }
            book.setAuthors(authorSet);
        }

        Book saved = bookRepository.save(book);
        return mapToDto(saved);
    }

    @Transactional
    public BookDto updateBook(Long id, BookDto dto) {
        Book book = getBookEntityById(id);

        book.setTitle(dto.getTitle());
        book.setSubtitle(dto.getSubtitle());
        book.setDescription(dto.getDescription());
        book.setIsbn10(dto.getIsbn10());
        book.setIsbn13(dto.getIsbn13());
        book.setPublicationDate(dto.getPublicationDate());
        book.setEdition(dto.getEdition());
        book.setLanguage(dto.getLanguage());
        book.setPageCount(dto.getPageCount());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setListPrice(dto.getListPrice());
        book.setSellingPrice(dto.getSellingPrice());
        book.setStockQuantity(dto.getStockQuantity());
        book.setFeatured(dto.isFeatured());
        book.setActive(dto.isActive());
        book.setBestseller(dto.isBestseller());
        book.setDigitalAvailable(dto.isDigitalAvailable());
        book.setDigitalContentType(dto.getDigitalContentType());
        book.setDigitalContentReference(dto.getDigitalContentReference());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            book.setCategory(category);
        } else {
            book.setCategory(null);
        }

        if (dto.getPublisherName() != null && !dto.getPublisherName().trim().isEmpty()) {
            Publisher publisher = publisherRepository.findByName(dto.getPublisherName().trim())
                    .orElseGet(() -> {
                        Publisher p = new Publisher();
                        p.setName(dto.getPublisherName().trim());
                        return publisherRepository.save(p);
                    });
            book.setPublisher(publisher);
        }

        if (dto.getAuthors() != null) {
            Set<Author> authorSet = new HashSet<>();
            for (AuthorDto aDto : dto.getAuthors()) {
                if (aDto.getName() != null && !aDto.getName().trim().isEmpty()) {
                    Author author = authorRepository.findByName(aDto.getName().trim())
                            .orElseGet(() -> {
                                Author a = new Author();
                                a.setName(aDto.getName().trim());
                                return authorRepository.save(a);
                            });
                    authorSet.add(author);
                }
            }
            book.setAuthors(authorSet);
        }

        Book updated = bookRepository.save(book);
        return mapToDto(updated);
    }

    @Transactional
    public BookDto updateBookStatus(Long id, boolean active) {
        Book book = getBookEntityById(id);
        book.setActive(active);
        return mapToDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto updateBookStock(Long id, int stockQuantity) {
        Book book = getBookEntityById(id);
        book.setStockQuantity(stockQuantity);
        return mapToDto(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookEntityById(id);
        bookRepository.delete(book);
    }

    public BookDto mapToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setSubtitle(book.getSubtitle());
        dto.setSlug(book.getSlug());
        dto.setDescription(book.getDescription());
        dto.setIsbn10(book.getIsbn10());
        dto.setIsbn13(book.getIsbn13());
        if (book.getPublisher() != null) {
            dto.setPublisherName(book.getPublisher().getName());
        }
        dto.setPublicationDate(book.getPublicationDate());
        dto.setEdition(book.getEdition());
        dto.setLanguage(book.getLanguage());
        dto.setPageCount(book.getPageCount());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        if (book.getCategory() != null) {
            dto.setCategoryId(book.getCategory().getId());
            dto.setCategoryName(book.getCategory().getName());
        }
        if (book.getAuthors() != null) {
            dto.setAuthors(book.getAuthors().stream()
                    .map(a -> new AuthorDto(a.getId(), a.getName()))
                    .collect(Collectors.toList()));
        }
        dto.setListPrice(book.getListPrice());
        dto.setSellingPrice(book.getSellingPrice());
        dto.setStockQuantity(book.getStockQuantity());
        dto.setAverageRating(book.getAverageRating());
        dto.setReviewCount(book.getReviewCount());
        dto.setFeatured(book.isFeatured());
        dto.setActive(book.isActive());
        dto.setBestseller(book.isBestseller());
        dto.setDigitalAvailable(book.isDigitalAvailable());
        dto.setDigitalContentType(book.getDigitalContentType());
        dto.setDigitalContentReference(book.getDigitalContentReference());
        dto.setExternalSource(book.getExternalSource());
        dto.setExternalSourceId(book.getExternalSourceId());
        return dto;
    }

    public BookSummaryDto mapToSummaryDto(Book book) {
        BookSummaryDto dto = new BookSummaryDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setSlug(book.getSlug());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            dto.setAuthorName(book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", ")));
        } else {
            dto.setAuthorName("Unknown Author");
        }
        if (book.getCategory() != null) {
            dto.setCategoryName(book.getCategory().getName());
        } else {
            dto.setCategoryName("General");
        }
        dto.setListPrice(book.getListPrice());
        dto.setSellingPrice(book.getSellingPrice());
        dto.setAverageRating(book.getAverageRating() != null ? book.getAverageRating() : BigDecimal.ZERO);
        dto.setReviewCount(book.getReviewCount() != null ? book.getReviewCount() : 0);
        dto.setInStock(book.getStockQuantity() != null && book.getStockQuantity() > 0);
        dto.setDigitalAvailable(book.isDigitalAvailable());
        return dto;
    }
}
