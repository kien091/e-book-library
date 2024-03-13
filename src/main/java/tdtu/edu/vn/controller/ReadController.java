//package tdtu.edu.vn.controller;
//
//import lombok.SneakyThrows;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tdtu.edu.vn.model.Document;
//import tdtu.edu.vn.service.ebook.DocumentService;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
//@RequestMapping("")
//public class ReadController {
//    @Autowired
//    private DocumentService documentService;
//
//    @SneakyThrows
//    @GetMapping("/read/{id}/pdf")
//    public ResponseEntity<Resource> getDocumentPdf(@PathVariable String id) {
//        // Assume documentService can fetch the document including the path to the PDF
//        Document document = documentService.getDocumentById(id);
//        Path path = Paths.get(document.getPdfUrl());
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(resource);
//    }
//
//
//}

package tdtu.edu.vn.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdtu.edu.vn.model.*;
import tdtu.edu.vn.service.ebook.*;
import tdtu.edu.vn.util.AESUtil;
import tdtu.edu.vn.util.JwtUtilsHelper;
import tdtu.edu.vn.util.PDFSecurity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("")
public class ReadController {
    private final DocumentService documentService;
    private ActivationCodeService activationCodeService;
    private UserService userService;
    private JwtUtilsHelper jwtUtilsHelper;
    private OrderService orderService;
    private EncodeDocumentService edService;

    public ReadController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/read/{id}/pdf")
    public ResponseEntity<Resource> getDocumentPdf(@PathVariable String id, HttpServletRequest request) {
        // Lấy token từ header
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // Lấy thông tin người dùng từ token
        String email = jwtUtilsHelper.getEmailFromToken(token);
        User user = userService.findByEmail(email);
        String userId = user.getId();
        // Kiểm tra xem người dùng có đơn hàng không
        Order order = orderService.findByUserIdAndBookId(userId, id); // modify
        if (order == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String orderId = order.getId();
        // Kiểm tra xem mã kích hoạt có tồn tại không
        ActivationCode activation = activationCodeService.findActivationCodeIdByOrderId(orderId);
        if (activation == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ActivationCode.ActivationCodeStatus status = activation.getStatus();

        Document document = documentService.getDocumentById(id);
        if (!document.getDrmEnabled()) {
            //tài liệu không phải DRM
            return serveDocument(document);
        }

        // Đối với các tài liệu hỗ trợ DRM, hãy kiểm tra kích hoạt và quyền người dùng
        if (status != ActivationCode.ActivationCodeStatus.USED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // auto enter password (change this if logic isn't correct)
        EncodeDocument encodeDocument = edService.findByDocumentId(id);
        PDFSecurity.autoEnterPassword(document.getPdfUrl(), AESUtil.decrypt(encodeDocument.getPassword()));

        // Giả sử logic giải mã tài liệu được triển khai khi cần thiết
        return serveDocument(document);
    }

    private ResponseEntity<Resource> serveDocument(Document document) {
        try {
            Path path = Paths.get(document.getPdfUrl());
            if (!Files.exists(path)) {
                System.out.println("File does not exist at path: " + path);
                // Trả về lỗi 500 nếu không thể tìm thấy tài liệu

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Đọc tài liệu từ đĩa
            byte[] documentBytes = Files.readAllBytes(path);
            // Giả sử tài liệu được mã hóa, giải mã tài liệu nếu cần
            if (document.getDrmEnabled()) {
                //Giả sử phương thức decryptDocumentBytes được triển khai khi cần thiết
                documentBytes = decryptDocumentBytes(documentBytes, "minhphuong9902");
            }

            ByteArrayResource resource = new ByteArrayResource(documentBytes);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            // Trả về lỗi 500 nếu có lỗi khi đọc tài liệu
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private byte[] decryptDocumentBytes(byte[] documentBytes, String yourActivationCode) {
        //Giả sử logic giải mã được thực hiện khi cần thiết
        return documentBytes;
    }

}