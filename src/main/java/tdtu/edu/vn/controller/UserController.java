package tdtu.edu.vn.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdtu.edu.vn.Payload.ResponseData;
import tdtu.edu.vn.model.User;
import tdtu.edu.vn.service.ebook.UserService;
import tdtu.edu.vn.util.JwtUtilsHelper;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    UserService userService;
    JwtUtilsHelper jwtUtilsHelper;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        ResponseData responseData = new ResponseData();

        if (user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username and password are required fields");
        }

        if (userService.checkLogin(user)) {
            User userDb = userService.findByEmail(user.getEmail());

            String token = jwtUtilsHelper.generateToken(userDb.getEmail(), userDb.getRole());
            responseData.setData(token);

            return ResponseEntity.ok(responseData);
        } else {
            responseData.setData(null);
            responseData.setSuccess(false);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
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

    @PostMapping("/user")
    public ResponseEntity<?> getUser() {
        System.out.println("Get user");

        return ResponseEntity.ok("User");
    }
}
