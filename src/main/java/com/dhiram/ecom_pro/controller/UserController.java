package com.dhiram.ecom_pro.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhiram.ecom_pro.dto.EmailResendRequest;
import com.dhiram.ecom_pro.model.User;
import com.dhiram.ecom_pro.repo.UserRepo;
import com.dhiram.ecom_pro.service.TwilioSmsService;
import com.dhiram.ecom_pro.utils.JwtUtil;
import com.dhiram.ecom_pro.utils.PasswordBCrypt;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    // @Autowired
    // private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TwilioSmsService smsService;

    @GetMapping("/test-connection")
    public String testConnection() {
        try {
            userRepo.count(); // Simple query to test connection
            return "Database connection is working!";
        } catch (Exception e) {
            return "Connection failed: " + e.getMessage();
        }
    }

    @PostMapping("/register")
    public User createUser(@Valid @RequestBody User user) {
        user.setPassword(PasswordBCrypt.hashPassword(user.getPassword()));
        return userRepo.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsers(@RequestBody Map<String, String> user) {
        String email = user.get("email");
        String password = user.get("password");

        Map<String, Object> sentData = new HashMap<>();

        // Find user by email
        Optional<User> foundUser = userRepo.findByEmail(email);
        if (foundUser.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!PasswordBCrypt.matchesPassword(password, foundUser.get().getPassword())) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
        List<String> roles = List.of("ROLE_USER");

        sentData.put("status", "Login successful");
        sentData.put("email", foundUser.get().getEmail());
        sentData.put("name", foundUser.get().getName());
        sentData.put("id", foundUser.get().getId());
        sentData.put("token", jwtUtil.generateToken(email, roles, 0));

        return new ResponseEntity<>(sentData, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> userForgotPassword(@Valid @RequestBody EmailResendRequest emailResponse) {
        try {
            // ResponseEntity<?> buyerUser = userService.userForgotPassword(emailResponse);
            smsService.sendSms("+917284947022", "Yo Nigam your order shipped!");;
            return null;
            // return buyerUser;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to process password reset request",
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now()));
        }
    }
}
