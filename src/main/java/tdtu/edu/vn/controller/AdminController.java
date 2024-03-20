package tdtu.edu.vn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tdtu.edu.vn.model.*;
import tdtu.edu.vn.service.ebook.*;
import tdtu.edu.vn.util.AESUtil;
import tdtu.edu.vn.util.PDFSecurity;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin")
public class AdminController {
    private AuthorService authorService;
    private CategoryService categoryService;
    private DocumentService documentService;
    private PublisherService publisherService;
    private ActivationCodeService activationCodeService;
    private UserService userService;
    private OrderService orderService;
    private BCryptPasswordEncoder passwordEncoder;
    private EncodeDocumentService edService;
    private EmailService emailService;


    // CRUD for author
    @PostMapping("/create-author")
    public ResponseEntity<Author> createAuthor(@RequestBody Author newAuthor) {
        newAuthor.setId(null);
        Author savedAuthor = authorService.createAuthor(newAuthor);
        if (savedAuthor == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(savedAuthor);
    }

    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        if (authors == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/author/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable String id) {
        Author author = authorService.getAuthorById(id);
        if (author == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(author);
    }

    @PostMapping("/update-author") // should be PATCH method
    public ResponseEntity<Author> updateAuthor(@RequestBody Author updatedAuthor) {
        Author existingAuthor = authorService.getAuthorById(updatedAuthor.getId());
        if (existingAuthor == null) {
            return ResponseEntity.notFound().build();
        }
        Author savedAuthor = authorService.updateAuthor(updatedAuthor);
        if (savedAuthor == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(savedAuthor);
    }

    @PostMapping("/delete-author") // should DELETE method
    public ResponseEntity<Author> deleteAuthor(@RequestBody Author author) {
        Author existingAuthor = authorService.getAuthorById(author.getId());
        if (existingAuthor == null) {
            return ResponseEntity.notFound().build();
        }
        if (authorService.deleteAuthor(author.getId())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    // CRUD for category
    @PostMapping("/create-category")
    public ResponseEntity<Category> createCategory(@RequestBody Category newCategory) {
        newCategory.setId(null);
        Category savedCategory = categoryService.createCategory(newCategory);
        if (savedCategory == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(savedCategory);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(categories);
    }


    @GetMapping("/category/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping("/update-category") // should be PATCH method
    public ResponseEntity<Category> updateCategory(@RequestBody Category updatedCategory) {
        Category existingCategory = categoryService.getCategoryById(updatedCategory.getId());
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }
        Category savedCategory = categoryService.updateCategory(updatedCategory);
        if (savedCategory == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(savedCategory);
    }

    @PostMapping("/delete-category") // should DELETE method
    public ResponseEntity<Category> deleteCategory(@RequestBody Category category) {
        Category existingCategory = categoryService.getCategoryById(category.getId());
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }
        if (categoryService.deleteCategory(category.getId())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    // CRUD for document
    @PostMapping("/create-document")
    public ResponseEntity<Document> createDocument(@RequestBody Document newBook) {
        newBook.setId(null);

        Document savedDocument = documentService.createDocument(newBook);

        if (savedDocument == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // encrypt pdf if drm enabled
        if(savedDocument.getDrmEnabled()){
            EncodeDocument encodeDocument = new EncodeDocument(savedDocument.getId(), AESUtil.encrypt(generatePassword()));
            edService.createEncodeDocument(encodeDocument);

            System.out.println("The passsword: " + encodeDocument.getPassword() + "\ncrypt: " + AESUtil.decrypt(encodeDocument.getPassword()));
            PDFSecurity.encryptPDF(
                    savedDocument.getPdfUrl(),
                    savedDocument.getPdfUrl(),
                    AESUtil.decrypt(encodeDocument.getPassword()));
        }

        return ResponseEntity.ok(savedDocument);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.getAllDocuments();

        if (documents == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(documents);
    }

    @GetMapping("/document/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable String id) {
        Document document = documentService.getDocumentById(id);

        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(document);
    }

    @PostMapping("/update-document")
    public ResponseEntity<Document> updateDocument(@RequestBody Document updatedBook) {
        Document existingDocument = documentService.getDocumentById(updatedBook.getId());

        if (existingDocument == null) {
            return ResponseEntity.notFound().build();
        }

        Document savedDocument = documentService.updateDocument(updatedBook);

        if (savedDocument == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(savedDocument);
    }

    @PostMapping("/delete-document")
    public ResponseEntity<Document> deleteDocument(@RequestBody Document document) {
        Document existingDocument = documentService.getDocumentById(document.getId());

        if (existingDocument == null) {
            return ResponseEntity.notFound().build();
        }

        if (documentService.deleteDocument(document.getId())) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    // CRUD for Order


    @PostMapping("/create-order")
    public ResponseEntity<Order> createOrder(@RequestBody Order newOrder) {
        newOrder.setId(null);
        newOrder.setOrderDate(new Date());
        Order savedOrder = orderService.createOrder(newOrder);

        if (savedOrder == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responses = new ArrayList<>();
        List<Order> orders = orderService.getAllOrders();

        if (orders == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        for (Order order : orders) {
            User user = userService.getUserById(order.getUserId());
            List<Document> documents = new ArrayList<>();

            for (String bookId : order.getBookIds()) {
                try {
                    documents.add(documentService.getDocumentById(bookId));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            ActivationCode activationCode = null;
            if (order.getActivationCodeId() != null) {
                activationCode = activationCodeService.getActivationCodeById(order.getActivationCodeId());
            }
            responses.add(new OrderResponse(order, user, documents, activationCode));
        }

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        Order order = orderService.getOrderById(id);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.getUserById(order.getUserId());
        List<Document> documents = new ArrayList<>();

        for (String bookId : order.getBookIds()) {
            try {
                documents.add(documentService.getDocumentById(bookId));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        ActivationCode activationCode = null;
        if (order.getActivationCodeId() != null) {
            activationCode = activationCodeService.getActivationCodeById(order.getActivationCodeId());
        }

        return ResponseEntity.ok(new OrderResponse(order, user, documents, activationCode));
    }

    @PostMapping("/update-order") // should be PATCH
    public ResponseEntity<Order> updateOrder(@RequestBody Order updatedOrder) {
        Order existingOrder = orderService.getOrderById(updatedOrder.getId());

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        updatedOrder.setOrderDate(existingOrder.getOrderDate());
        Order savedOrder = orderService.updateOrder(updatedOrder);


        if (savedOrder == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/delete-order")
    public ResponseEntity<Order> deleteOrder(@RequestBody Order order) {
        Order existingOrder = orderService.getOrderById(order.getId());

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        if (orderService.deleteOrder(order)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class OrderResponse {
        Order order;
        User user;
        List<Document> documents;
        ActivationCode activationCode;
    }


    // CRUD for Publisher
    @PostMapping("/create-publisher")
    public ResponseEntity<Publisher> createPublisher(@RequestBody Publisher newPublisher) {
        newPublisher.setId(null);
        Publisher savedPublisher = publisherService.createPublisher(newPublisher);
        if (savedPublisher == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(savedPublisher);
    }

    @GetMapping("/publishers")
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        List<Publisher> publishers = publisherService.getAllPublishers();
        if (publishers == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/publisher/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable String id) {
        Publisher publisher = publisherService.getPublisherById(id);
        if (publisher == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(publisher);
    }

    @PostMapping("/update-publisher") // should be PATCH method
    public ResponseEntity<Publisher> updatePublisher(@RequestBody Publisher updatedPublisher) {
        Publisher existingPublisher = publisherService.getPublisherById(updatedPublisher.getId());
        if (existingPublisher == null) {
            return ResponseEntity.notFound().build();
        }
        Publisher savedPublisher = publisherService.updatePublisher(updatedPublisher);
        if (savedPublisher == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(savedPublisher);
    }


    @PostMapping("/delete-publisher") // should DELETE method
    public ResponseEntity<Publisher> deletePublisher(@RequestBody Publisher publisher) {
        Publisher existingPublisher = publisherService.getPublisherById(publisher.getId());
        if (existingPublisher == null) {
            return ResponseEntity.notFound().build();
        }
        if (publisherService.deletePublisher(publisher.getId())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



    // CRUD for User
    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(
            @RequestParam Map<String, String> userDetails,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {
        try {
            // Tạo một đối tượng User mới từ userDetails
            User user = new User();
            // Đặt các trường thông tin người dùng từ userDetails
            user.setUsername(userDetails.get("username"));
            user.setPosition(userDetails.get("position"));
            user.setEmail(userDetails.get("email"));
            user.setPhone(userDetails.get("phone"));
            user.setPassword(userDetails.get("password"));
            user.setConfirmPassword(userDetails.get("confirmPassword"));
            user.setFullname(userDetails.get("fullname"));
            user.setAddress(userDetails.get("address"));
            user.setSex(userDetails.get("sex"));
            user.setBirthday(userDetails.get("birthday"));
            user.setSubscribe(userDetails.get("subscribe"));
            user.setRole(userDetails.get("role"));
            user.setLocked(Boolean.parseBoolean(userDetails.get("isLocked")));

            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Xử lý file ở đây: lưu trữ và cập nhật đường dẫn hoặc lưu trực tiếp byte[]
                byte[] avatarBytes = avatarFile.getBytes();
                user.setAvatar(avatarBytes); // Lưu trữ byte[] vào avatar
            }

            User createdUser = userService.createUser(user); // Gọi hàm tạo người dùng mới trong service
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        if (users == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/update-user")
    public ResponseEntity<?> updateUser(
            @RequestParam Map<String, String> userDetails,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {
        try {
            // Xử lý thông tin người dùng từ userDetails
            String userId = userDetails.get("id"); // Giả sử bạn gửi id (nếu đây là cập nhật)
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            // Cập nhật các trường thông tin người dùng từ userDetails
            user.setUsername(userDetails.get("username"));
            user.setPosition(userDetails.get("position"));
            user.setEmail(userDetails.get("email"));
            user.setPhone(userDetails.get("phone"));
            user.setPassword(userDetails.get("password"));
            user.setConfirmPassword(userDetails.get("confirmPassword"));
            user.setFullname(userDetails.get("fullname"));
            user.setAddress(userDetails.get("address"));
            user.setSex(userDetails.get("sex"));
            user.setBirthday(userDetails.get("birthday"));
            user.setSubscribe(userDetails.get("subscribe"));
            user.setRole(userDetails.get("role"));
            user.setLocked(Boolean.parseBoolean(userDetails.get("isLocked")));

            if (avatarFile != null && !avatarFile.isEmpty()) {
                // Xử lý file ở đây: lưu trữ và cập nhật đường dẫn hoặc lưu trực tiếp byte[]
                byte[] avatarBytes = avatarFile.getBytes();
                user.setAvatar(avatarBytes); // Lưu trữ byte[] vào avatar
            }

            User updatedUser = userService.updateUser(user); // Gọi hàm cập nhật người dùng trong service
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/delete-user") // should DELETE method
    public ResponseEntity<User> deleteUser(@RequestBody User user, Principal principal) {
        if (principal.getName().equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User existingUser = userService.findByEmail(user.getEmail());

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (userService.deleteUser(user.getId())) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }





    // Another methods (not CRUD)

    // Change user password
    @PostMapping("/change-user-password")
    public ResponseEntity<User> changeUserPassword(@RequestBody User user) {
        User existingUser = userService.findByEmail(user.getEmail());

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setConfirmPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.updateUser(existingUser);

        if (savedUser == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(savedUser);
    }

    private String generatePassword() {
        Random random = new Random();
        int length = 16;

        // with 10 digits and 26 characters (use ascii code to convert to character)
        return System.currentTimeMillis() + random.ints(length, 0, 36)
                .mapToObj(i -> i < 10 ? String.valueOf(i) : String.valueOf((char) (i + 55)))
                .collect(Collectors.joining());
    }

    // accept order (change status of activation code)
    @PostMapping("/accept-order")
    public ResponseEntity<Order> acceptOrder(@RequestBody Order order) {
        Order existingOrder = orderService.getOrderById(order.getId());

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        existingOrder.setOrderStatus(Order.OrderStatus.ACCEPTED);
        orderService.updateOrder(existingOrder);

        ActivationCode activationCode = activationCodeService.getActivationCodeById(existingOrder.getActivationCodeId());
        activationCode.setStatus(ActivationCode.ActivationCodeStatus.USED);
        activationCodeService.updateActivationCode(activationCode);

        // send activation code to user's email
        try {
            User user = userService.getUserById(existingOrder.getUserId());
            String subject = "Activation code for your order";
            String body = "Your activation code is: " + activationCode.getCode();
            emailService.sendEmail("nguyntrungkin091@gmail.com", user.getEmail(), subject, body);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok(existingOrder);
    }

    // cancel order (delete activation code)
    @PostMapping("/cancel-order")
    public ResponseEntity<Order> cancelOrder(@RequestBody Order order) {
        Order existingOrder = orderService.getOrderById(order.getId());

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        existingOrder.setOrderStatus(Order.OrderStatus.CANCELLED);
        orderService.updateOrder(existingOrder);

        ActivationCode activationCode = activationCodeService.getActivationCodeById(existingOrder.getActivationCodeId());
        activationCodeService.deleteActivationCode(activationCode.getId());

        return ResponseEntity.ok(existingOrder);
    }
}

