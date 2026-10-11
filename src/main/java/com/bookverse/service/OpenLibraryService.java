package com.bookverse.service;

import com.bookverse.dto.*;
import com.bookverse.entity.Author;
import com.bookverse.entity.Book;
import com.bookverse.entity.Category;
import com.bookverse.entity.Publisher;
import com.bookverse.entity.MetadataImportJob;
import com.bookverse.enums.ImportStatus;
import com.bookverse.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenLibraryService {

    private static final Logger log = LoggerFactory.getLogger(OpenLibraryService.class);

    private final MetadataImportJobRepository importJobRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final BookService bookService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${bookverse.open-library.base-url:https://openlibrary.org}")
    private String openLibraryBaseUrl;

    public OpenLibraryService(MetadataImportJobRepository importJobRepository,
                              BookRepository bookRepository,
                              AuthorRepository authorRepository,
                              CategoryRepository categoryRepository,
                              PublisherRepository publisherRepository,
                              BookService bookService,
                              RestTemplateBuilder restTemplateBuilder,
                              ObjectMapper objectMapper) {
        this.importJobRepository = importJobRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.bookService = bookService;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(12))
                .setReadTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * Universal Book Search:
     * Queries both local store MySQL database and Open Library global catalog.
     * Accurately distinguishes local inventory from external discovery results.
     */
    public UniversalSearchResponseDto universalSearch(String query, String title, String author,
                                                      String isbn, String subject, int page, int limit) {
        String mainQuery = (query != null) ? query.trim() : "";
        int safePage = Math.max(1, page);
        int safeLimit = Math.max(5, Math.min(limit, 50));

        // 1. Search local BOOKVERSE store inventory
        List<BookSummaryDto> localMatches = new ArrayList<>();
        if (!mainQuery.isEmpty() || (title != null && !title.isBlank()) || (author != null && !author.isBlank())) {
            String storeKeyword = !mainQuery.isEmpty() ? mainQuery : (title != null ? title : author);
            Page<BookSummaryDto> localPage = bookService.searchBooks(
                    storeKeyword, null, null, null, null, null, null, null,
                    PageRequest.of(0, 10)
            );
            localMatches = localPage.getContent();
        }

        // 2. Query Open Library Search API
        List<DiscoveredBookDto> discoveredList = new ArrayList<>();
        long totalFound = 0;
        boolean hasMore = false;

        try {
            List<String> queryParams = new ArrayList<>();
            if (isbn != null && !isbn.isBlank()) {
                queryParams.add("isbn=" + URLEncoder.encode(isbn.trim(), StandardCharsets.UTF_8));
            } else {
                if (title != null && !title.isBlank()) {
                    queryParams.add("title=" + URLEncoder.encode(title.trim(), StandardCharsets.UTF_8));
                }
                if (author != null && !author.isBlank()) {
                    queryParams.add("author=" + URLEncoder.encode(author.trim(), StandardCharsets.UTF_8));
                }
                if (subject != null && !subject.isBlank()) {
                    queryParams.add("subject=" + URLEncoder.encode(subject.trim(), StandardCharsets.UTF_8));
                }
                if (!mainQuery.isEmpty() && queryParams.isEmpty()) {
                    queryParams.add("q=" + URLEncoder.encode(mainQuery, StandardCharsets.UTF_8));
                }
            }

            if (queryParams.isEmpty()) {
                queryParams.add("q=" + URLEncoder.encode("classics", StandardCharsets.UTF_8));
            }

            queryParams.add("page=" + safePage);
            queryParams.add("limit=" + safeLimit);

            String url = openLibraryBaseUrl + "/search.json?" + String.join("&", queryParams);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "BookverseApp/1.0 (contact@bookverse.com)");
            headers.set("Accept", "application/json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                totalFound = root.path("numFound").asLong(0);
                JsonNode docs = root.path("docs");

                if (docs.isArray()) {
                    for (JsonNode doc : docs) {
                        DiscoveredBookDto bookDto = parseDocToDiscoveredDto(doc);
                        if (bookDto != null) {
                            discoveredList.add(bookDto);
                        }
                    }
                }
                hasMore = (totalFound > (long) safePage * safeLimit);
            }
        } catch (Exception e) {
            log.warn("Open Library search request failed: {}", e.getMessage());
        }

        return new UniversalSearchResponseDto(
                mainQuery,
                totalFound + localMatches.size(),
                safePage,
                safeLimit,
                localMatches,
                discoveredList,
                hasMore
        );
    }

    /**
     * Parse an Open Library JSON doc into a DiscoveredBookDto,
     * cross-referencing local store inventory to identify duplicates or existing products.
     */
    private DiscoveredBookDto parseDocToDiscoveredDto(JsonNode doc) {
        String title = doc.path("title").asText(null);
        if (title == null || title.trim().isEmpty()) {
            return null;
        }

        DiscoveredBookDto dto = new DiscoveredBookDto();
        dto.setTitle(title.trim());
        dto.setSubtitle(doc.path("subtitle").asText(null));
        dto.setKey(doc.path("key").asText(""));
        dto.setFirstPublishYear(doc.path("first_publish_year").asInt(0));
        dto.setEditionCount(doc.path("edition_count").asInt(1));
        dto.setPageCount(doc.path("number_of_pages_median").asInt(0));
        dto.setRatingsAverage(doc.path("ratings_average").asDouble(0.0));

        // Authors
        List<String> authors = new ArrayList<>();
        JsonNode authorArr = doc.path("author_name");
        if (authorArr.isArray()) {
            for (JsonNode a : authorArr) {
                String an = a.asText().trim();
                if (!an.isEmpty()) authors.add(an);
                if (authors.size() >= 3) break;
            }
        }
        if (authors.isEmpty()) authors.add("Various Authors");
        dto.setAuthors(authors);
        dto.setAuthorName(String.join(", ", authors));

        // ISBNs
        JsonNode isbnArr = doc.path("isbn");
        if (isbnArr.isArray()) {
            for (JsonNode isb : isbnArr) {
                String val = isb.asText("").replaceAll("[^0-9X]", "");
                if (val.length() == 13 && dto.getIsbn13() == null) {
                    dto.setIsbn13(val);
                } else if (val.length() == 10 && dto.getIsbn10() == null) {
                    dto.setIsbn10(val);
                }
            }
        }

        // Cover
        int coverId = doc.path("cover_i").asInt(0);
        dto.setCoverId(coverId);
        if (coverId > 0) {
            dto.setCoverImageUrl("https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg");
        } else {
            dto.setCoverImageUrl("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80");
        }

        // Publisher
        JsonNode pubArr = doc.path("publisher");
        if (pubArr.isArray() && pubArr.size() > 0) {
            dto.setPublisher(pubArr.get(0).asText());
        }

        // Subjects
        List<String> subs = new ArrayList<>();
        JsonNode subArr = doc.path("subject");
        if (subArr.isArray()) {
            for (JsonNode s : subArr) {
                String st = s.asText().trim();
                if (!st.isEmpty()) subs.add(st);
                if (subs.size() >= 5) break;
            }
        }
        dto.setSubjects(subs);

        // Check if BOOKVERSE already stocks this book in MySQL
        Optional<Book> localMatch = Optional.empty();
        if (dto.getIsbn13() != null) {
            localMatch = bookRepository.findByIsbn13(dto.getIsbn13());
        }
        if (localMatch.isEmpty() && dto.getIsbn10() != null) {
            localMatch = bookRepository.findByIsbn10(dto.getIsbn10());
        }
        if (localMatch.isEmpty() && dto.getKey() != null && !dto.getKey().isEmpty()) {
            localMatch = bookRepository.findByExternalSourceAndExternalSourceId("OPEN_LIBRARY", dto.getKey());
        }
        if (localMatch.isEmpty()) {
            localMatch = bookRepository.findFirstByTitleIgnoreCase(title.trim());
        }

        if (localMatch.isPresent()) {
            Book b = localMatch.get();
            dto.setInStore(true);
            dto.setStoreBookId(b.getId());
            dto.setStoreSlug(b.getSlug());
            dto.setSellingPrice(b.getSellingPrice());
            dto.setListPrice(b.getListPrice());
            dto.setStockQuantity(b.getStockQuantity());
            dto.setActive(b.isActive());
        } else {
            dto.setInStore(false);
        }

        return dto;
    }

    /**
     * Retrieve full details of an Open Library Work by key.
     */
    public DiscoveryWorkDetailDto getWorkDetails(String workKey) {
        String cleanKey = workKey.trim();
        if (!cleanKey.startsWith("/")) {
            cleanKey = "/" + cleanKey;
        }

        DiscoveryWorkDetailDto dto = new DiscoveryWorkDetailDto();
        dto.setKey(cleanKey);

        try {
            String url = openLibraryBaseUrl + cleanKey + ".json";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "BookverseApp/1.0 (contact@bookverse.com)");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                dto.setTitle(root.path("title").asText(""));

                // Description can be a String or an Object {type:..., value:...}
                JsonNode descNode = root.path("description");
                if (descNode.isTextual()) {
                    dto.setDescription(descNode.asText());
                } else if (descNode.isObject() && descNode.has("value")) {
                    dto.setDescription(descNode.path("value").asText());
                }

                // Subjects
                List<String> subjects = new ArrayList<>();
                JsonNode subArr = root.path("subjects");
                if (subArr.isArray()) {
                    for (JsonNode s : subArr) {
                        subjects.add(s.asText());
                        if (subjects.size() >= 8) break;
                    }
                }
                dto.setSubjects(subjects);

                // Covers
                List<String> covers = new ArrayList<>();
                JsonNode covArr = root.path("covers");
                if (covArr.isArray()) {
                    for (JsonNode c : covArr) {
                        int cid = c.asInt(0);
                        if (cid > 0) {
                            covers.add("https://covers.openlibrary.org/b/id/" + cid + "-L.jpg");
                        }
                    }
                }
                dto.setCovers(covers);

                // Check local store presence
                Optional<Book> localMatch = bookRepository.findByExternalSourceAndExternalSourceId("OPEN_LIBRARY", cleanKey);
                if (localMatch.isEmpty()) {
                    localMatch = bookRepository.findFirstByTitleIgnoreCase(dto.getTitle());
                }
                if (localMatch.isPresent()) {
                    Book b = localMatch.get();
                    dto.setInStore(true);
                    dto.setStoreBookId(b.getId());
                    dto.setStoreSlug(b.getSlug());
                } else {
                    dto.setInStore(false);
                }
            }
        } catch (Exception e) {
            log.error("Failed to retrieve work details for key: {}", cleanKey, e);
        }

        return dto;
    }

    /**
     * Administrator-only single book import action.
     * Saves validated Open Library metadata to MySQL with admin-configured price, stock, and listing status.
     */
    @Transactional
    public BookDto importDiscoveredBook(AdminBookImportDto dto) {
        String title = dto.getTitle().trim();

        // 1. Check for duplicates
        if (dto.getIsbn13() != null && !dto.getIsbn13().isBlank()) {
            Optional<Book> existing = bookRepository.findByIsbn13(dto.getIsbn13().trim());
            if (existing.isPresent()) {
                Book b = existing.get();
                // Update pricing and stock as requested by admin
                if (dto.getSellingPrice() != null) b.setSellingPrice(dto.getSellingPrice());
                if (dto.getListPrice() != null) b.setListPrice(dto.getListPrice());
                if (dto.getStockQuantity() != null) b.setStockQuantity(dto.getStockQuantity());
                b.setActive(dto.isActive());
                return bookService.mapToDto(bookRepository.save(b));
            }
        }

        if (dto.getOpenLibraryKey() != null && !dto.getOpenLibraryKey().isBlank()) {
            Optional<Book> existing = bookRepository.findByExternalSourceAndExternalSourceId("OPEN_LIBRARY", dto.getOpenLibraryKey().trim());
            if (existing.isPresent()) {
                Book b = existing.get();
                if (dto.getSellingPrice() != null) b.setSellingPrice(dto.getSellingPrice());
                if (dto.getListPrice() != null) b.setListPrice(dto.getListPrice());
                if (dto.getStockQuantity() != null) b.setStockQuantity(dto.getStockQuantity());
                b.setActive(dto.isActive());
                return bookService.mapToDto(bookRepository.save(b));
            }
        }

        // 2. Generate slug
        String baseSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        if (baseSlug.length() > 100) baseSlug = baseSlug.substring(0, 100);
        if (baseSlug.isEmpty()) baseSlug = "book-" + System.currentTimeMillis();

        String slug = baseSlug;
        int count = 1;
        while (bookRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count++;
        }

        // 3. Authors resolution
        Set<Author> authors = new HashSet<>();
        List<String> authorNames = new ArrayList<>();
        if (dto.getAuthors() != null && !dto.getAuthors().isEmpty()) {
            authorNames.addAll(dto.getAuthors());
        } else if (dto.getAuthorName() != null && !dto.getAuthorName().isBlank()) {
            String[] split = dto.getAuthorName().split(",");
            for (String s : split) {
                if (!s.trim().isEmpty()) authorNames.add(s.trim());
            }
        }

        if (authorNames.isEmpty()) authorNames.add("Various Authors");

        for (String aName : authorNames) {
            String cleanName = aName.trim();
            Author author = authorRepository.findByNameIgnoreCase(cleanName)
                    .orElseGet(() -> {
                        Author newA = new Author();
                        newA.setName(cleanName);
                        return authorRepository.save(newA);
                    });
            authors.add(author);
        }

        // 4. Category resolution
        Category category = null;
        if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {
            String cName = dto.getCategoryName().trim();
            category = categoryRepository.findByNameIgnoreCase(cName)
                    .orElseGet(() -> {
                        Category newC = new Category();
                        newC.setName(cName);
                        String cSlug = cName.toLowerCase().replaceAll("[^a-z0-9-]", "-");
                        newC.setSlug(categoryRepository.existsBySlug(cSlug) ? cSlug + "-" + System.currentTimeMillis() : cSlug);
                        newC.setActive(true);
                        return categoryRepository.save(newC);
                    });
        }

        if (category == null) {
            List<Category> allCats = categoryRepository.findAll();
            category = !allCats.isEmpty() ? allCats.get(0) : null;
        }

        // 5. Publisher resolution
        Publisher publisher = null;
        if (dto.getPublisherName() != null && !dto.getPublisherName().isBlank()) {
            String pName = dto.getPublisherName().trim();
            publisher = publisherRepository.findByNameIgnoreCase(pName)
                    .orElseGet(() -> {
                        Publisher newP = new Publisher();
                        newP.setName(pName);
                        return publisherRepository.save(newP);
                    });
        }

        // 6. Build and persist Book
        Book book = new Book();
        book.setTitle(title);
        book.setSubtitle(dto.getSubtitle());
        book.setSlug(slug);
        book.setIsbn10(dto.getIsbn10());
        book.setIsbn13(dto.getIsbn13());
        book.setPublisher(publisher);
        book.setCategory(category);
        book.setAuthors(authors);
        book.setCoverImageUrl(dto.getCoverImageUrl() != null && !dto.getCoverImageUrl().isBlank()
                ? dto.getCoverImageUrl()
                : "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80");
        book.setDescription(dto.getDescription() != null && !dto.getDescription().isBlank()
                ? dto.getDescription()
                : "Curated work discovered from Open Library catalog.");

        int pubYear = dto.getPublicationYear() != null ? dto.getPublicationYear() : 2022;
        book.setPublicationDate(LocalDate.of(Math.max(1800, Math.min(2026, pubYear)), 1, 1));
        book.setPageCount(dto.getPageCount() != null && dto.getPageCount() > 0 ? dto.getPageCount() : 250);

        book.setListPrice(dto.getListPrice() != null ? dto.getListPrice() : BigDecimal.valueOf(499.00));
        book.setSellingPrice(dto.getSellingPrice() != null ? dto.getSellingPrice() : BigDecimal.valueOf(399.00));
        book.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 20);

        book.setActive(dto.isActive());
        book.setFeatured(dto.isFeatured());
        book.setBestseller(dto.isBestseller());
        book.setDigitalAvailable(dto.isDigitalAvailable());
        book.setExternalSource("OPEN_LIBRARY");
        book.setExternalSourceId(dto.getOpenLibraryKey());

        Book saved = bookRepository.save(book);
        log.info("Administrator imported book to MySQL store: '{}' (ID: {}) with price ₹{}, stock {}",
                saved.getTitle(), saved.getId(), saved.getSellingPrice(), saved.getStockQuantity());
        return bookService.mapToDto(saved);
    }

    public List<MetadataImportJobDto> getImportHistory() {
        return importJobRepository.findAllByOrderByStartedAtDesc().stream()
                .map(this::mapJobToDto)
                .collect(Collectors.toList());
    }

    public MetadataImportJobDto getImportJobById(Long id) {
        return importJobRepository.findById(id)
                .map(this::mapJobToDto)
                .orElse(null);
    }

    /**
     * Batch import jobs from Open Library by keyword or genre.
     */
    public MetadataImportJobDto importBooks(ImportRequestDto request) {
        String query = request.getQuery().trim();
        int limit = request.getLimit() != null ? Math.min(request.getLimit(), 50) : 10;

        MetadataImportJob job = new MetadataImportJob();
        job.setSourceName("OPEN_LIBRARY");
        job.setSearchQuery(query);
        job.setRequestedCount(limit);
        job.setStatus(ImportStatus.IN_PROGRESS);
        job = importJobRepository.save(job);

        int imported = 0;
        int skipped = 0;
        int failed = 0;
        StringBuilder errorLog = new StringBuilder();

        try {
            String url = openLibraryBaseUrl + "/search.json?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&limit=" + limit;

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "BookverseApp/1.0 (contact@bookverse.com)");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode docs = root.path("docs");

                if (docs.isArray()) {
                    for (JsonNode doc : docs) {
                        try {
                            boolean wasImported = processDoc(doc, query);
                            if (wasImported) {
                                imported++;
                            } else {
                                skipped++;
                            }
                        } catch (Exception ex) {
                            failed++;
                            errorLog.append("Error parsing book: ").append(ex.getMessage()).append("; ");
                            log.error("Failed to process OpenLibrary book item", ex);
                        }
                    }
                }
            } else {
                failed = limit;
                errorLog.append("External API returned status: ").append(response.getStatusCode());
            }

            job.setStatus(ImportStatus.COMPLETED);
        } catch (Exception e) {
            log.error("OpenLibrary import job encountered an error", e);
            job.setStatus(ImportStatus.FAILED);
            errorLog.append("API Request failed: ").append(e.getMessage());
            failed = limit - imported - skipped;
        }

        job.setImportedCount(imported);
        job.setSkippedCount(skipped);
        job.setFailureCount(failed);
        job.setErrorSummary(errorLog.length() > 0 ? errorLog.toString() : "Import completed successfully");
        job.setFinishedAt(LocalDateTime.now());

        return mapJobToDto(importJobRepository.save(job));
    }

    @Transactional
    public boolean processDoc(JsonNode doc, String fallbackCategoryName) {
        String title = doc.path("title").asText(null);
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        title = title.trim();

        // Check ISBN
        String isbn13 = null;
        String isbn10 = null;
        JsonNode isbnArray = doc.path("isbn");
        if (isbnArray.isArray() && isbnArray.size() > 0) {
            for (JsonNode isbnNode : isbnArray) {
                String candidate = isbnNode.asText("").replaceAll("[^0-9X]", "");
                if (candidate.length() == 13 && isbn13 == null) {
                    isbn13 = candidate;
                } else if (candidate.length() == 10 && isbn10 == null) {
                    isbn10 = candidate;
                }
            }
        }

        // Deduplication check
        if (isbn13 != null && bookRepository.existsByIsbn13(isbn13)) {
            return false;
        }

        String baseSlug = title.toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+", "-");
        if (baseSlug.length() > 100) baseSlug = baseSlug.substring(0, 100);
        String slug = baseSlug;
        if (bookRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + System.currentTimeMillis();
        }

        // Authors
        Set<Author> authors = new HashSet<>();
        JsonNode authorsArray = doc.path("author_name");
        if (authorsArray.isArray() && authorsArray.size() > 0) {
            for (JsonNode authorNode : authorsArray) {
                String authorName = authorNode.asText().trim();
                if (!authorName.isEmpty()) {
                    Author a = authorRepository.findByName(authorName)
                            .orElseGet(() -> {
                                Author newA = new Author();
                                newA.setName(authorName);
                                return authorRepository.save(newA);
                            });
                    authors.add(a);
                    if (authors.size() >= 2) break;
                }
            }
        }

        if (authors.isEmpty()) {
            Author fallbackAuthor = authorRepository.findByName("Open Library Contributors")
                    .orElseGet(() -> {
                        Author a = new Author();
                        a.setName("Open Library Contributors");
                        return authorRepository.save(a);
                    });
            authors.add(fallbackAuthor);
        }

        // Category
        Category category = null;
        JsonNode subjects = doc.path("subject");
        if (subjects.isArray() && subjects.size() > 0) {
            String subject = subjects.get(0).asText().trim();
            if (subject.length() > 40) subject = subject.substring(0, 40);
            String catSlug = subject.toLowerCase().replaceAll("[^a-z0-9-]", "-");
            category = categoryRepository.findBySlug(catSlug).orElse(null);
        }

        if (category == null) {
            String catSlug = fallbackCategoryName.toLowerCase().replaceAll("[^a-z0-9-]", "-");
            category = categoryRepository.findBySlug(catSlug).orElse(null);
        }

        if (category == null) {
            List<Category> allCats = categoryRepository.findAll();
            if (!allCats.isEmpty()) {
                category = allCats.get(0);
            }
        }

        // Cover image
        String coverUrl = null;
        int coverI = doc.path("cover_i").asInt(0);
        if (coverI > 0) {
            coverUrl = "https://covers.openlibrary.org/b/id/" + coverI + "-L.jpg";
        }

        int pageCount = doc.path("number_of_pages_median").asInt(280);
        int publishYear = doc.path("first_publish_year").asInt(2022);
        LocalDate pubDate = LocalDate.of(Math.max(1800, Math.min(2026, publishYear)), 1, 1);

        Book book = new Book();
        book.setTitle(title);
        book.setSlug(slug);
        book.setIsbn10(isbn10);
        book.setIsbn13(isbn13);
        book.setPageCount(pageCount > 0 ? pageCount : 250);
        book.setPublicationDate(pubDate);
        book.setCoverImageUrl(coverUrl != null ? coverUrl : "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80");
        book.setCategory(category);
        book.setAuthors(authors);
        book.setDescription("Imported from Open Library database. An insightful work on " + fallbackCategoryName + ".");
        book.setListPrice(BigDecimal.valueOf(499.00));
        book.setSellingPrice(BigDecimal.valueOf(399.00));
        book.setStockQuantity(20);
        book.setActive(true);
        book.setFeatured(false);
        book.setDigitalAvailable(false);
        book.setExternalSource("OPEN_LIBRARY");
        book.setExternalSourceId(doc.path("key").asText(""));

        bookRepository.save(book);
        return true;
    }

    private MetadataImportJobDto mapJobToDto(MetadataImportJob job) {
        MetadataImportJobDto dto = new MetadataImportJobDto();
        dto.setId(job.getId());
        dto.setSourceName(job.getSourceName());
        dto.setSearchQuery(job.getSearchQuery());
        dto.setRequestedCount(job.getRequestedCount());
        dto.setImportedCount(job.getImportedCount());
        dto.setSkippedCount(job.getSkippedCount());
        dto.setFailureCount(job.getFailureCount());
        dto.setStatus(job.getStatus());
        dto.setErrorSummary(job.getErrorSummary());
        dto.setStartedAt(job.getStartedAt());
        dto.setFinishedAt(job.getFinishedAt());
        return dto;
    }
}
