package com.bookverse.service;

import com.bookverse.entity.*;
import com.bookverse.enums.DigitalContentType;
import com.bookverse.enums.RoleType;
import com.bookverse.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataInitializerService {

    private static final Logger log = LoggerFactory.getLogger(DataInitializerService.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final CouponRepository couponRepository;
    private final DigitalContentRepository digitalContentRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bookverse.admin.email:admin@bookverse.com}")
    private String adminEmail;

    @Value("${bookverse.admin.password:Admin@123456}")
    private String adminPassword;

    @Value("${bookverse.admin.name:System Administrator}")
    private String adminName;

    public DataInitializerService(RoleRepository roleRepository,
                                  UserRepository userRepository,
                                  CategoryRepository categoryRepository,
                                  AuthorRepository authorRepository,
                                  PublisherRepository publisherRepository,
                                  BookRepository bookRepository,
                                  CartRepository cartRepository,
                                  WishlistRepository wishlistRepository,
                                  CouponRepository couponRepository,
                                  DigitalContentRepository digitalContentRepository,
                                  PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
        this.wishlistRepository = wishlistRepository;
        this.couponRepository = couponRepository;
        this.digitalContentRepository = digitalContentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("Checking database initialization status...");

        // 1. Initialize Roles
        Role roleUser = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_USER)));
        Role roleAdmin = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_ADMIN)));

        // 2. Initialize Admin & Demo Users
        initUsers(roleUser, roleAdmin);

        // 3. Initialize Coupons
        initCoupons();

        // 4. Initialize Categories & Books
        if (categoryRepository.count() == 0 || bookRepository.count() == 0) {
            initCatalog();
        }

        log.info("Database initialization completed successfully. Total books: {}", bookRepository.count());
    }

    private void initUsers(Role roleUser, Role roleAdmin) {
        if (!userRepository.existsByEmail(adminEmail.toLowerCase().trim())) {
            User admin = new User();
            admin.setEmail(adminEmail.toLowerCase().trim());
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setFullName(adminName);
            admin.setPhoneNumber("+91 9876543210");
            admin.setActive(true);
            admin.setRoles(new HashSet<>(Arrays.asList(roleUser, roleAdmin)));
            User savedAdmin = userRepository.save(admin);

            Cart cart = new Cart();
            cart.setUser(savedAdmin);
            cartRepository.save(cart);

            Wishlist wishlist = new Wishlist();
            wishlist.setUser(savedAdmin);
            wishlistRepository.save(wishlist);
            log.info("Default administrator account initialized: {}", adminEmail);
        }

        String demoStudentEmail = "student@bookverse.com";
        if (!userRepository.existsByEmail(demoStudentEmail)) {
            User student = new User();
            student.setEmail(demoStudentEmail);
            student.setPasswordHash(passwordEncoder.encode("Student@123456"));
            student.setFullName("Arjun Sharma");
            student.setPhoneNumber("+91 9876543211");
            student.setActive(true);
            student.setRoles(new HashSet<>(Collections.singletonList(roleUser)));
            User savedStudent = userRepository.save(student);

            Cart cart = new Cart();
            cart.setUser(savedStudent);
            cartRepository.save(cart);

            Wishlist wishlist = new Wishlist();
            wishlist.setUser(savedStudent);
            wishlistRepository.save(wishlist);
            log.info("Demo student customer account initialized: {}", demoStudentEmail);
        }
    }

    private void initCoupons() {
        if (couponRepository.count() == 0) {
            Coupon c1 = new Coupon();
            c1.setCode("BOOKVERSE10");
            c1.setDiscountPercentage(BigDecimal.valueOf(10.00));
            c1.setMinimumOrderAmount(BigDecimal.valueOf(399.00));
            c1.setValidFrom(LocalDateTime.now().minusDays(1));
            c1.setValidUntil(LocalDateTime.now().plusMonths(6));
            c1.setActive(true);
            couponRepository.save(c1);

            Coupon c2 = new Coupon();
            c2.setCode("WELCOME50");
            c2.setFlatDiscountAmount(BigDecimal.valueOf(50.00));
            c2.setMinimumOrderAmount(BigDecimal.valueOf(299.00));
            c2.setValidFrom(LocalDateTime.now().minusDays(1));
            c2.setValidUntil(LocalDateTime.now().plusMonths(6));
            c2.setActive(true);
            couponRepository.save(c2);

            Coupon c3 = new Coupon();
            c3.setCode("STUDENT20");
            c3.setDiscountPercentage(BigDecimal.valueOf(20.00));
            c3.setMinimumOrderAmount(BigDecimal.valueOf(599.00));
            c3.setValidFrom(LocalDateTime.now().minusDays(1));
            c3.setValidUntil(LocalDateTime.now().plusMonths(6));
            c3.setActive(true);
            couponRepository.save(c3);
        }
    }

    private void initCatalog() {
        log.info("Seeding comprehensive bookstore catalog...");

        // Categories
        Category catProg = getOrCreateCategory("Programming", "programming", "Coding and software engineering languages", null, 1);
        Category catWeb = getOrCreateCategory("Web Development", "web-development", "Modern frontend and backend frameworks", null, 2);
        Category catCS = getOrCreateCategory("Computer Science", "computer-science", "Core computer science fundamentals", null, 3);
        Category catAcad = getOrCreateCategory("Academics", "academics", "University curriculum, mathematics and competitive exams", null, 4);
        Category catLit = getOrCreateCategory("Literature & Fiction", "literature", "Classic, contemporary, and genre fiction", null, 5);
        Category catPhil = getOrCreateCategory("Philosophy", "philosophy", "Ancient and modern philosophical inquiries", null, 6);
        Category catSelf = getOrCreateCategory("Self-Help & Psychology", "self-help", "Personal growth, habits, and psychology", null, 7);
        getOrCreateCategory("Business & Economics", "business", "Finance, strategy, and economics", null, 8);

        // Subcategories
        Category subJava = getOrCreateCategory("Java", "java", "Java ecosystem", catProg, 1);
        getOrCreateCategory("Python", "python", "Python language", catProg, 2);
        Category subCpp = getOrCreateCategory("C / C++", "c-cpp", "C and C++ systems programming", catProg, 3);
        Category subDSA = getOrCreateCategory("Data Structures & Algorithms", "data-structures-algorithms", "DSA fundamentals", catProg, 4);
        Category subSpring = getOrCreateCategory("Spring Boot & Backend", "spring-boot", "Enterprise backend development", catWeb, 5);
        Category subOS = getOrCreateCategory("Operating Systems", "operating-systems", "System architecture & OS", catCS, 6);
        getOrCreateCategory("Database Systems", "database-systems", "Relational and NoSQL DBMS", catCS, 7);
        Category subAI = getOrCreateCategory("AI & Machine Learning", "ai-machine-learning", "Artificial intelligence and deep learning", catCS, 8);
        Category subMath = getOrCreateCategory("Mathematics & Stats", "mathematics", "Discrete math and statistics", catAcad, 9);
        Category subClassics = getOrCreateCategory("Classic Literature", "classics", "Timeless public-domain masterpieces", catLit, 10);
        Category subEthics = getOrCreateCategory("Ethics & Logic", "ethics-logic", "Philosophical principles and logic", catPhil, 11);
        Category subHabits = getOrCreateCategory("Productivity & Habits", "habits-productivity", "High performance and personal growth", catSelf, 12);

        // Publishers
        Publisher pubPearson = getOrCreatePublisher("Pearson Education");
        getOrCreatePublisher("O'Reilly Media");
        Publisher pubManning = getOrCreatePublisher("Manning Publications");
        Publisher pubMIT = getOrCreatePublisher("MIT Press");
        Publisher pubPenguin = getOrCreatePublisher("Penguin Random House");
        Publisher pubOxford = getOrCreatePublisher("Oxford University Press");
        getOrCreatePublisher("HarperCollins");

        // Authors
        Author authBloch = getOrCreateAuthor("Joshua Bloch", "Former Chief Java Architect at Google.");
        Author authMartin = getOrCreateAuthor("Robert C. Martin", "Uncle Bob, legendary software craftsman.");
        Author authCormen = getOrCreateAuthor("Thomas H. Cormen", "Professor of Computer Science at Dartmouth College.");
        Author authSilberschatz = getOrCreateAuthor("Abraham Silberschatz", "Professor of Computer Science at Yale University.");
        Author authRussell = getOrCreateAuthor("Stuart Russell", "Professor of Computer Science at UC Berkeley.");
        Author authNorvig = getOrCreateAuthor("Peter Norvig", "Director of Research at Google.");
        Author authTanenbaum = getOrCreateAuthor("Andrew S. Tanenbaum", "Renowned computer science author and professor.");
        Author authAusten = getOrCreateAuthor("Jane Austen", "English novelist known for realism and social commentary.");
        Author authShelley = getOrCreateAuthor("Mary Shelley", "English novelist who wrote Frankenstein.");
        Author authPlato = getOrCreateAuthor("Plato", "Classical Greek philosopher and founder of the Academy in Athens.");
        Author authClear = getOrCreateAuthor("James Clear", "Writer and speaker focused on habits and decision-making.");
        Author authKahneman = getOrCreateAuthor("Daniel Kahneman", "Nobel Memorial Prize laureate in Economic Sciences.");
        Author authWalls = getOrCreateAuthor("Craig Walls", "Software engineer and author of Spring in Action.");
        Author authSedgewick = getOrCreateAuthor("Robert Sedgewick", "Professor of Computer Science at Princeton University.");
        Author authStroustrup = getOrCreateAuthor("Bjarne Stroustrup", "Creator of the C++ programming language.");

        // Books Creation
        // 1. Effective Java
        createBook("Effective Java", "effective-java",
                "The definitive guide to Java platform best practices, updated for Java 7, 8, 9 and beyond.",
                "0134685997", "9780134685991", pubPearson, LocalDate.of(2018, 1, 1), "3rd Edition", 416,
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80",
                subJava, Collections.singletonList(authBloch),
                BigDecimal.valueOf(899.00), BigDecimal.valueOf(749.00), 25, BigDecimal.valueOf(4.90), 128,
                true, true, false, null, null);

        // 2. Clean Code
        createBook("Clean Code: A Handbook of Agile Software Craftsmanship", "clean-code",
                "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.",
                "0132350882", "9780132350884", pubPearson, LocalDate.of(2008, 8, 1), "1st Edition", 464,
                "https://images.unsplash.com/photo-1532012164546-f432f2e3777f?auto=format&fit=crop&w=600&q=80",
                catProg, Collections.singletonList(authMartin),
                BigDecimal.valueOf(799.00), BigDecimal.valueOf(649.00), 30, BigDecimal.valueOf(4.85), 210,
                true, true, false, null, null);

        // 3. Introduction to Algorithms (CLRS)
        createBook("Introduction to Algorithms", "introduction-to-algorithms-clrs",
                "A comprehensive textbook that covers a broad range of algorithms in depth, yet makes their design accessible.",
                "026204630X", "9780262046305", pubMIT, LocalDate.of(2022, 4, 5), "4th Edition", 1312,
                "https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=600&q=80",
                subDSA, Collections.singletonList(authCormen),
                BigDecimal.valueOf(1499.00), BigDecimal.valueOf(1299.00), 18, BigDecimal.valueOf(4.95), 340,
                true, true, false, null, null);

        // 4. Operating System Concepts
        createBook("Operating System Concepts", "operating-system-concepts",
                "The fundamental concepts of operating systems, processes, memory management, and virtualization.",
                "1119800366", "9781119800361", pubPearson, LocalDate.of(2021, 5, 1), "10th Edition", 976,
                "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=600&q=80",
                subOS, Collections.singletonList(authSilberschatz),
                BigDecimal.valueOf(950.00), BigDecimal.valueOf(799.00), 12, BigDecimal.valueOf(4.75), 95,
                false, true, false, null, null);

        // 5. Artificial Intelligence: A Modern Approach
        createBook("Artificial Intelligence: A Modern Approach", "artificial-intelligence-modern-approach",
                "The authoritative, most widely used introduction to the theory and practice of artificial intelligence.",
                "0134610997", "9780134610993", pubPearson, LocalDate.of(2020, 4, 28), "4th Edition", 1152,
                "https://images.unsplash.com/photo-1507842229451-79b1be8d1a20?auto=format&fit=crop&w=600&q=80",
                subAI, Arrays.asList(authRussell, authNorvig),
                BigDecimal.valueOf(1250.00), BigDecimal.valueOf(1099.00), 15, BigDecimal.valueOf(4.90), 180,
                true, true, false, null, null);

        // 6. Spring in Action
        createBook("Spring in Action", "spring-in-action",
                "Hands-on guide to building full-stack enterprise applications using Spring Boot and Java.",
                "1617297577", "9781617297571", pubManning, LocalDate.of(2022, 2, 1), "6th Edition", 520,
                "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=600&q=80",
                subSpring, Collections.singletonList(authWalls),
                BigDecimal.valueOf(850.00), BigDecimal.valueOf(699.00), 22, BigDecimal.valueOf(4.80), 85,
                true, false, false, null, null);

        // 7. Atomic Habits
        createBook("Atomic Habits: An Easy & Proven Way to Build Good Habits & Break Bad Ones", "atomic-habits",
                "Transform your life with small daily changes and breakthrough habit systems.",
                "0735211299", "9780735211292", pubPenguin, LocalDate.of(2018, 10, 16), "1st Edition", 320,
                "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=600&q=80",
                subHabits, Collections.singletonList(authClear),
                BigDecimal.valueOf(599.00), BigDecimal.valueOf(449.00), 40, BigDecimal.valueOf(4.92), 420,
                true, true, false, null, null);

        // 8. Thinking, Fast and Slow
        createBook("Thinking, Fast and Slow", "thinking-fast-and-slow",
                "Exploration of the two systems that drive the way we think: fast intuitive thinking and slow deliberate thinking.",
                "0374533555", "9780374533557", pubPenguin, LocalDate.of(2013, 4, 2), "Reprint Edition", 512,
                "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=600&q=80",
                catSelf, Collections.singletonList(authKahneman),
                BigDecimal.valueOf(650.00), BigDecimal.valueOf(499.00), 20, BigDecimal.valueOf(4.70), 190,
                false, true, false, null, null);

        // 9. Pride and Prejudice (Public Domain / Digital Reader)
        Book pride = createBook("Pride and Prejudice", "pride-and-prejudice",
                "Jane Austen's timeless romantic classic charting the emotional development of Elizabeth Bennet.",
                "0141439513", "9780141439518", pubPenguin, LocalDate.of(1813, 1, 28), "Classic Edition", 432,
                "https://images.unsplash.com/photo-1476275466078-4007374efbbe?auto=format&fit=crop&w=600&q=80",
                subClassics, Collections.singletonList(authAusten),
                BigDecimal.valueOf(299.00), BigDecimal.valueOf(199.00), 50, BigDecimal.valueOf(4.88), 310,
                true, true, true, "PUBLIC_DOMAIN_TEXT", "Gutenberg #1342");

        createDigitalContent(pride,
                "### Pride and Prejudice\n*By Jane Austen*\n\n"
                        + "#### Chapter 1\n\n"
                        + "It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.\n\n"
                        + "However little known the feelings or views of such a man may be on his first entering a neighbourhood, this truth is so well fixed in the minds of the surrounding families, that he is considered the rightful property of some one or other of their daughters.\n\n"
                        + "\"My dear Mr. Bennet,\" said his lady to him one day, \"have you heard that Netherfield Park is let at last?\"\n\n"
                        + "Mr. Bennet replied that he had not.\n\n"
                        + "\"But it is,\" returned she; \"for Mrs. Long has just been here, and she told me all about it.\"\n\n"
                        + "Mr. Bennet made no answer.\n\n"
                        + "\"Do you not want to know who has taken it?\" cried his wife impatiently.\n\n"
                        + "\"You want to tell me, and I have no objection to hearing it.\"\n\n"
                        + "This was invitation enough.\n\n"
                        + "#### Chapter 2\n\n"
                        + "Mr. Bennet was among the earliest of those who waited on Mr. Bingley. He had always intended to visit him, though to the last always assuring his wife that he should not go; and till the evening after the visit was paid she had no knowledge of it.");

        // 10. Frankenstein (Public Domain / Digital Reader)
        Book frankenstein = createBook("Frankenstein; or, The Modern Prometheus", "frankenstein",
                "Mary Shelley's foundational gothic science fiction masterpiece about Victor Frankenstein and his creature.",
                "0141439475", "9780141439471", pubPenguin, LocalDate.of(1818, 1, 1), "Gothic Classic", 280,
                "https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=600&q=80",
                subClassics, Collections.singletonList(authShelley),
                BigDecimal.valueOf(299.00), BigDecimal.valueOf(199.00), 35, BigDecimal.valueOf(4.82), 245,
                true, true, true, "PUBLIC_DOMAIN_TEXT", "Gutenberg #84");

        createDigitalContent(frankenstein,
                "### Frankenstein; or, The Modern Prometheus\n*By Mary Wollstonecraft Shelley*\n\n"
                        + "#### Letter 1\n*To Mrs. Saville, England.*\n\n"
                        + "St. Petersburgh, Dec. 11th, 17—.\n\n"
                        + "You will rejoice to hear that no disaster has accompanied the commencement of an enterprise which you have regarded with such evil forebodings. I arrived here yesterday, and my first task is to assure my dear sister of my welfare and increasing confidence in the success of my undertaking.\n\n"
                        + "#### Chapter 1\n\n"
                        + "I am by birth a Genevese, and my family is one of the most distinguished of that republic. My ancestors had been for many years counsellors and syndics, and my father had filled several public situations with honour and reputation.");

        // 11. The Republic (Plato)
        Book republic = createBook("The Republic", "the-republic-plato",
                "Plato's foundational Socratic dialogue on justice, the order and character of the just city-state, and the just man.",
                "0140455116", "9780140455113", pubOxford, LocalDate.of(1955, 1, 1), "Penguin Classics", 416,
                "https://images.unsplash.com/photo-1531988042231-d39a9cc12a9a?auto=format&fit=crop&w=600&q=80",
                subEthics, Collections.singletonList(authPlato),
                BigDecimal.valueOf(350.00), BigDecimal.valueOf(250.00), 20, BigDecimal.valueOf(4.78), 150,
                false, true, true, "PUBLIC_DOMAIN_TEXT", "Gutenberg #1497");

        createDigitalContent(republic,
                "### The Republic\n*By Plato (Translated by Benjamin Jowett)*\n\n"
                        + "#### Book I\n\n"
                        + "I went down yesterday to the Piraeus with Glaucon the son of Ariston, that I might offer up my prayers to the goddess; and also because I wanted to see in what manner they would celebrate the festival, which was a new thing.\n\n"
                        + "I was delighted with the procession of the inhabitants; but that of the Thracians was equally, if not more, beautiful. When we had finished our prayers and viewed the spectacle, we turned in the direction of the city.");

        // 12. Algorithms (4th Edition) by Sedgewick
        createBook("Algorithms", "algorithms-4th-edition",
                "The leading textbook on algorithms today, widely used in colleges and universities worldwide.",
                "032157351X", "9780321573513", pubPearson, LocalDate.of(2011, 3, 19), "4th Edition", 992,
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=600&q=80",
                subDSA, Collections.singletonList(authSedgewick),
                BigDecimal.valueOf(1199.00), BigDecimal.valueOf(999.00), 16, BigDecimal.valueOf(4.86), 110,
                false, true, false, null, null);

        // 13. The C++ Programming Language
        createBook("The C++ Programming Language", "cpp-programming-language",
                "The definitive reference guide written by the creator of the language, Bjarne Stroustrup.",
                "0321563840", "9780321563842", pubPearson, LocalDate.of(2013, 5, 19), "4th Edition", 1376,
                "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=600&q=80",
                subCpp, Collections.singletonList(authStroustrup),
                BigDecimal.valueOf(1350.00), BigDecimal.valueOf(1150.00), 14, BigDecimal.valueOf(4.80), 90,
                false, true, false, null, null);

        // 14. Modern Operating Systems
        createBook("Modern Operating Systems", "modern-operating-systems",
                "Widely acclaimed introduction to the principles and practice of operating systems.",
                "013359162X", "9780133591620", pubPearson, LocalDate.of(2014, 3, 10), "4th Edition", 1136,
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?auto=format&fit=crop&w=600&q=80",
                subOS, Collections.singletonList(authTanenbaum),
                BigDecimal.valueOf(999.00), BigDecimal.valueOf(849.00), 15, BigDecimal.valueOf(4.77), 80,
                false, true, false, null, null);

        // 15. Discrete Mathematics and Its Applications
        createBook("Discrete Mathematics and Its Applications", "discrete-mathematics-and-applications",
                "Focused on mathematical reasoning, combinatorial analysis, discrete structures, and algorithmic thinking.",
                "125967651X", "9781259676512", pubPearson, LocalDate.of(2018, 7, 1), "8th Edition", 1120,
                "https://images.unsplash.com/photo-1509228468518-180dd4864904?auto=format&fit=crop&w=600&q=80",
                subMath, Collections.singletonList(authSedgewick),
                BigDecimal.valueOf(999.00), BigDecimal.valueOf(849.00), 20, BigDecimal.valueOf(4.82), 140,
                false, true, false, null, null);

        // 16. The Almanack of Naval Ravikant (Business / Self-Help / Philosophy)
        Author authJorgenson = getOrCreateAuthor("Eric Jorgenson", "Writer, entrepreneur, and curator of Naval Ravikant's wisdom.");
        Publisher pubMagrathea = getOrCreatePublisher("Magrathea Publishing");
        createBook("The Almanack of Naval Ravikant", "the-almanack-of-naval-ravikant",
                "A guide to wealth and happiness curated from the wisdom of Silicon Valley investor Naval Ravikant.",
                "1544514212", "9781544514215", pubMagrathea, LocalDate.of(2020, 9, 15), "1st Edition", 244,
                "https://covers.openlibrary.org/b/id/10449931-L.jpg",
                catSelf, Collections.singletonList(authJorgenson),
                BigDecimal.valueOf(550.00), BigDecimal.valueOf(399.00), 45, BigDecimal.valueOf(4.95), 520,
                true, true, false, null, null);

        // 17. The Metamorphosis by Franz Kafka (Classic Literature / Existential Fiction)
        Author authKafka = getOrCreateAuthor("Franz Kafka", "Bohemian novelist and short-story writer, widely regarded as one of the major figures of 20th-century literature.");
        Book metamorphosis = createBook("The Metamorphosis", "the-metamorphosis-kafka",
                "Franz Kafka's landmark novella recounting the story of salesman Gregor Samsa, who awakens transformed into an insect-like creature.",
                "0143105248", "9780143105244", pubPenguin, LocalDate.of(1915, 10, 1), "Penguin Classics", 128,
                "https://covers.openlibrary.org/b/id/12820198-L.jpg",
                subClassics, Collections.singletonList(authKafka),
                BigDecimal.valueOf(299.00), BigDecimal.valueOf(199.00), 35, BigDecimal.valueOf(4.88), 410,
                true, true, true, "PUBLIC_DOMAIN_TEXT", "Kafka #1");

        createDigitalContent(metamorphosis,
                "### The Metamorphosis\n*By Franz Kafka (Translated by David Wyllie)*\n\n"
                        + "#### Chapter I\n\n"
                        + "One morning, when Gregor Samsa woke from troubled dreams, he found himself transformed in his bed into a horrible vermin.\n\n"
                        + "He lay on his armour-like back, and if he lifted his head a little he could see his brown belly, slightly domed and divided by arches into stiff sections. The bedding was hardly able to cover it and seemed ready to slide off any moment.\n\n"
                        + "His many legs, pitifully thin compared with the size of the rest of him, waved about helplessly as he looked.\n\n"
                        + "\"What's happened to me?\" he thought. It wasn't a dream. His room, a proper human room although a little too small, lay peacefully between its four familiar walls.");

        // 18. Meditations by Marcus Aurelius (Philosophy / Stoicism)
        Author authAurelius = getOrCreateAuthor("Marcus Aurelius", "Roman Emperor and prominent Stoic philosopher whose personal writings became a timeless guide to resilience.");
        Book meditations = createBook("Meditations", "meditations-marcus-aurelius",
                "A series of personal reflections and Stoic philosophical writings by Roman Emperor Marcus Aurelius on virtue, duty, and peace of mind.",
                "0140449338", "9780140449334", pubPenguin, LocalDate.of(2006, 4, 27), "Penguin Classics", 256,
                "https://covers.openlibrary.org/b/id/13202688-L.jpg",
                subEthics, Collections.singletonList(authAurelius),
                BigDecimal.valueOf(350.00), BigDecimal.valueOf(249.00), 40, BigDecimal.valueOf(4.92), 630,
                true, true, true, "PUBLIC_DOMAIN_TEXT", "Marcus Aurelius #1");

        createDigitalContent(meditations,
                "### Meditations\n*By Marcus Aurelius (Translated by George Long)*\n\n"
                        + "#### Book I\n\n"
                        + "1. From my grandfather Verus I learned good morals and the government of my temper.\n\n"
                        + "2. From the reputation and remembrance of my father, modesty and a manly character.\n\n"
                        + "3. From my mother, piety and beneficence, and abstinence, not only from evil deeds, but even from evil thoughts; and further, simplicity in my way of living, far removed from the habits of the rich.\n\n"
                        + "#### Book II\n\n"
                        + "When you wake up in the morning, tell yourself: The people I deal with today will be meddling, ungrateful, arrogant, dishonest, jealous, and surly. They are like this because they cannot distinguish good from evil. But I have seen the beauty of good, and the ugliness of evil, and have recognized that the wrongdoer has a nature related to my own.");
    }

    private Category getOrCreateCategory(String name, String slug, String desc, Category parent, int order) {
        return categoryRepository.findBySlug(slug)
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setSlug(slug);
                    c.setDescription(desc);
                    c.setParent(parent);
                    c.setDisplayOrder(order);
                    c.setActive(true);
                    return categoryRepository.save(c);
                });
    }

    private Publisher getOrCreatePublisher(String name) {
        return publisherRepository.findByName(name)
                .orElseGet(() -> {
                    Publisher p = new Publisher();
                    p.setName(name);
                    return publisherRepository.save(p);
                });
    }

    private Author getOrCreateAuthor(String name, String bio) {
        return authorRepository.findByName(name)
                .orElseGet(() -> {
                    Author a = new Author();
                    a.setName(name);
                    a.setBiography(bio);
                    return authorRepository.save(a);
                });
    }

    private Book createBook(String title, String slug, String desc, String isbn10, String isbn13,
                           Publisher publisher, LocalDate pubDate, String edition, int pages,
                           String coverUrl, Category category, List<Author> authors,
                           BigDecimal listPrice, BigDecimal sellingPrice, int stock,
                           BigDecimal rating, int reviewCount, boolean featured, boolean bestseller,
                           boolean digitalAvailable, String digitalType, String digitalRef) {
        return bookRepository.findBySlug(slug).orElseGet(() -> {
            Book book = new Book();
            book.setTitle(title);
            book.setSlug(slug);
            book.setDescription(desc);
            book.setIsbn10(isbn10);
            book.setIsbn13(isbn13);
            book.setPublisher(publisher);
            book.setPublicationDate(pubDate);
            book.setEdition(edition);
            book.setPageCount(pages);
            book.setCoverImageUrl(coverUrl);
            book.setCategory(category);
            book.setAuthors(new HashSet<>(authors));
            book.setListPrice(listPrice);
            book.setSellingPrice(sellingPrice);
            book.setStockQuantity(stock);
            book.setAverageRating(rating);
            book.setReviewCount(reviewCount);
            book.setFeatured(featured);
            book.setBestseller(bestseller);
            book.setActive(true);
            book.setDigitalAvailable(digitalAvailable);
            book.setDigitalContentType(digitalType);
            book.setDigitalContentReference(digitalRef);
            return bookRepository.save(book);
        });
    }

    private void createDigitalContent(Book book, String text) {
        if (!digitalContentRepository.findByBookId(book.getId()).isPresent()) {
            DigitalContent dc = new DigitalContent();
            dc.setBook(book);
            dc.setContentType(DigitalContentType.PUBLIC_DOMAIN_TEXT);
            dc.setFullText(text);
            dc.setRightsNotice("Public Domain - Project Gutenberg authorized reading edition.");
            digitalContentRepository.save(dc);
        }
    }
}
