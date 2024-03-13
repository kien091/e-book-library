package tdtu.edu.vn.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tdtu.edu.vn.model.Document;
import tdtu.edu.vn.model.EncodeDocument;
import tdtu.edu.vn.model.User;
import tdtu.edu.vn.service.ebook.DocumentService;
import tdtu.edu.vn.service.ebook.EncodeDocumentService;
import tdtu.edu.vn.service.ebook.UserService;
import tdtu.edu.vn.util.AESUtil;
import tdtu.edu.vn.util.PDFSecurity;

import java.security.Principal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/admin")
public class AdminController {
    private DocumentService documentService;
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;
    private EncodeDocumentService edService;

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

            PDFSecurity.encryptPDF(
                    savedDocument.getPdfUrl(),
                    savedDocument.getPdfUrl(),
                    encodeDocument.getPassword());
        }

        return ResponseEntity.ok(savedDocument);
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

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllDocuments() {
        List<User> users = userService.getAllUsers();

        if (users == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(users);
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        newUser.setId(null);

        User savedUser = userService.createUser(newUser);

        if (savedUser == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/update-user")
    public ResponseEntity<User> updateUser(@RequestBody User updatedUser) {
        User existingUser = userService.findByEmail(updatedUser.getEmail());

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        User savedUser = userService.updateUser(updatedUser);

        if (savedUser == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/delete-user")
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
}
