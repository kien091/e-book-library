package tdtu.edu.vn.controller;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tdtu.edu.vn.Payload.ResponseData;
import tdtu.edu.vn.model.*;
import tdtu.edu.vn.repository.OTPCodeRepository;
import tdtu.edu.vn.repository.PasswordResetTokenRepository;
import tdtu.edu.vn.service.ebook.*;
import tdtu.edu.vn.util.JwtUtilsHelper;
import tdtu.edu.vn.Payload.ResponseData;
import tdtu.edu.vn.util.ResetPasswordRequest;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")

public class UserController {
     UserService userService;
     JwtUtilsHelper jwtUtilsHelper;
     ActivationCodeService activationCodeService;
     EmailService emailService;
     BCryptPasswordEncoder passwordEncoder;
     OTPCodeRepository otpCodeRepository;
     PasswordResetTokenRepository passwordResetTokenRepository;
     OrderService orderService;
     DocumentService documentService;

    private static final int OTP_LENGTH = 6;
    private Map<String, Map<String, Object>> resetPasswordDataMap = new ConcurrentHashMap<>();


    private String baseUrl ;

    @Autowired
    public UserController(UserService userService, JwtUtilsHelper jwtUtilsHelper, ActivationCodeService activationCodeService, EmailService emailService, BCryptPasswordEncoder passwordEncoder, OTPCodeRepository otpCodeRepository, PasswordResetTokenRepository passwordResetTokenRepository, OrderService orderService, DocumentService documentService) {
        this.userService = userService;
        this.jwtUtilsHelper = jwtUtilsHelper;
        this.activationCodeService = activationCodeService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.otpCodeRepository = otpCodeRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.orderService = orderService;
        this.documentService = documentService;
    }


    public String generateResetPasswordLink(User user) {
        // Use the hardcoded base URL
        return baseUrl + "/reset-password?email=" + user.getEmail();
    }

    public String generateOTP() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletRequest request) {
        ResponseData responseData = new ResponseData();

        if (user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password are required fields");
        }

        if (userService.checkLogin(user)) {
            User userDb = userService.findByEmail(user.getEmail());

            // Generate OTP
            String otp = generateOTP();

            otpCodeRepository.deleteByUserId(userDb.getId());

            // Save OTP to database
            OTPCode otpCode = new OTPCode();
            otpCode.setUserId(userDb.getId());
            otpCode.setCode(otp);
            otpCode.setCreatedAt(new Date()); // Lưu thời điểm tạo mã OTP
            otpCodeRepository.save(otpCode);

            // Send OTP to user's email
            try {
                emailService.sendOTPEmail(userDb.getEmail(), otp);
                System.out.println("OTP: " + otp);
            } catch (javax.mail.MessagingException e) {
                responseData.setSuccess(false);
                responseData.setMessage("Failed to send OTP email");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
            }

            responseData.setSuccess(true);
            responseData.setMessage("OTP sent to your email. Please enter the OTP to complete the login.");
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userDb.getId());
            responseData.setData(data);

            return ResponseEntity.ok(responseData);
        } else {
            responseData.setSuccess(false);
            responseData.setMessage("Invalid email or password");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
    }


    public void deleteExpiredOTPCodes() {
        Date oneMinuteAgo = new Date(System.currentTimeMillis() - 60000);
        List<OTPCode> expiredOTPCodes = otpCodeRepository.findByCreatedAtBefore(oneMinuteAgo);
        otpCodeRepository.deleteAll(expiredOTPCodes);
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        ResponseData responseData = new ResponseData();

        System.out.println("verifyOTP");
        System.out.println(requestBody);

        String otp = requestBody.get("otp");
        String userId = requestBody.get("userId");

        // Get the stored OTP from database
        OTPCode otpCode = otpCodeRepository.findByUserId(userId);

        if (otpCode == null || !otp.equals(otpCode.getCode())) {
            responseData.setSuccess(false);
            responseData.setMessage("Invalid OTP");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }

        // OTP is correct, generate JWT token
        User user = userService.getUserById(userId);
        String token = jwtUtilsHelper.generateToken(user.getEmail(), user.getRole(), user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("role", user.getRole());
        data.put("email", user.getEmail());
        responseData.setData(data);
        responseData.setSuccess(true);
        responseData.setMessage("Login successful");

        // Remove OTP from database
        otpCodeRepository.deleteByUserId(userId);

        return ResponseEntity.ok(responseData);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> requestBody) {
        ResponseData responseData = new ResponseData();
        String email = requestBody.get("email");

        User user = userService.findByEmail(email);
        if (user == null) {
            responseData.setSuccess(false);
            responseData.setMessage("Email not found");
            return ResponseEntity.badRequest().body(responseData);
        }

        // Generate reset password token
        String token = UUID.randomUUID().toString();

        // Delete old token for the user (if exists)
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Save new token to database
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour expiry
        passwordResetToken.setStatus(1); // Token created
        passwordResetTokenRepository.save(passwordResetToken);

        // Send reset password link to user's email
        String resetPasswordLink = "http://localhost:8080/reset-password?token=" + token;
        System.out.println("token: " + token);
        try {
            emailService.sendResetPasswordLinkEmail(user.getEmail(), resetPasswordLink);
            System.out.println("Reset Password Link: " + resetPasswordLink);
        } catch (MessagingException e) {
            responseData.setSuccess(false);
            responseData.setMessage("Failed to send reset password email");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        }

        responseData.setSuccess(true);
        responseData.setMessage("Reset password link sent to your email");

        return ResponseEntity.ok(responseData);
    }
    public void sendResetPasswordOTP( User input_user, HttpServletRequest request) throws MessagingException {
        User user = userService.findByEmail(input_user.getEmail());
        if (user == null) {
            // Xử lý trường hợp email không tồn tại
            return;
        }

        String otp = generateOTP();
        emailService.sendResetPasswordOTPEmail(user.getEmail(), otp);
        System.out.println("OTP: " + otp);

        // Lưu trữ OTP và user vào cấu trúc dữ liệu tạm thời
        Map<String, Object> resetPasswordData = new HashMap<>();
        resetPasswordData.put("otp", otp);
        resetPasswordData.put("user", user);
        // Sử dụng sessionId làm khóa để lưu trữ
        String sessionId = request.getSession().getId();
        resetPasswordDataMap.put(sessionId, resetPasswordData);
    }
    @PostMapping("/verify-reset-password-token")
    public ResponseEntity<?> verifyResetPasswordToken(@RequestBody Map<String, String> requestBody) {
        ResponseData responseData = new ResponseData();

        String token = requestBody.get("token");

        // Get the password reset token from database
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null || passwordResetToken.getExpiryDate().before(new Date()) || passwordResetToken.getStatus() != 1) {
            responseData.setSuccess(false);
            responseData.setMessage("Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }

        // Token is valid, update status to verified
        passwordResetToken.setStatus(2);
        passwordResetTokenRepository.save(passwordResetToken);

        responseData.setSuccess(true);
        responseData.setMessage("Token verification successful");

        return ResponseEntity.ok(responseData);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        ResponseData responseData = new ResponseData();
        String token = requestBody.get("token");
        String newPassword = requestBody.get("newPassword");

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null || passwordResetToken.getExpiryDate().before(new Date()) || passwordResetToken.getStatus() != 2) {
            responseData.setSuccess(false);
            responseData.setMessage("Invalid or unverified token");
            return ResponseEntity.badRequest().body(responseData);
        }

        User user = userService.getUserById(passwordResetToken.getUserId());
        if (user == null) {
            responseData.setSuccess(false);
            responseData.setMessage("User not found");
            return ResponseEntity.badRequest().body(responseData);
        }

        // Reset password
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);

        // Update token status to used
        passwordResetToken.setStatus(3);
        passwordResetTokenRepository.save(passwordResetToken);

        responseData.setSuccess(true);
        responseData.setMessage("Password reset successful");
        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/register")
    public ResponseEntity<String> PostRegister(@RequestBody User user) {
        try {
            String username = user.getUsername();
            String email = user.getEmail();
            String password = user.getPassword();
            String confirmPassword = user.getConfirmPassword();

            if (password == null || !password.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("Password and confirm password not match");
            }

            if (userService.isUserExists(username) || userService.isUserExists(email)){
                return ResponseEntity.badRequest().body("Username or email already exists");
            }

            userService.register(username, email, password, confirmPassword);

            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }




    //User: My Profile
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        if (user != null) {
            System.out.println(user);
            return ResponseEntity.ok(user);
        } else {

            System.out.println("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @SneakyThrows
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser_Profile(
            @PathVariable String id,
            @RequestPart("avatar") MultipartFile avatar,
            @RequestPart("phone") String phone,
            @RequestPart("address") String address,
            @RequestPart("sex") String sex,
            @RequestPart("fullname") String fullname,
            @RequestPart("birthday") String birthday,
            @RequestPart("subscribe") String subscribe) {

        try {
            User updateUser_Profile = new User();
            updateUser_Profile.setId(id);
            updateUser_Profile.setAvatar(avatar.getBytes());
            updateUser_Profile.setPhone(phone);
            updateUser_Profile.setAddress(address);
            updateUser_Profile.setSex(sex);
            updateUser_Profile.setFullname(fullname);
            updateUser_Profile.setBirthday(birthday);
            updateUser_Profile.setSubscribe(subscribe);

            if (!id.equals(updateUser_Profile.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            User user = userService.updateUser_Profile(updateUser_Profile);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/history")
    public ResponseEntity<?> getHistory(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtUtilsHelper.getEmailFromToken(token);
            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            List<Order> orders = orderService.getAllOrdersByUserId(user.getId());
            List<HistoryOrder> historyOrders = new ArrayList<>();
            for (Order order : orders) {
                HistoryOrder historyOrder = new HistoryOrder();
                historyOrder.order = order;
                List<Document> documents = new ArrayList<>();
                for (String bookId : order.getBookIds()) {
                    documents.add(documentService.getDocumentById(bookId));
                }
                historyOrder.documents = documents;
                historyOrder.activationCode = activationCodeService.findActivationCodeIdByOrderId(order.getId());
                historyOrders.add(historyOrder);
            }
            return ResponseEntity.ok(historyOrders);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }

    public static class HistoryOrder {
        Order order;
        List<Document> documents;
        ActivationCode activationCode;
    }
}
