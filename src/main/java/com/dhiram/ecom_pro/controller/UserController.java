package com.dhiram.ecom_pro.controller;

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

import com.dhiram.ecom_pro.model.User;
import com.dhiram.ecom_pro.repo.UserRepo;
import com.dhiram.ecom_pro.utils.JwtUtil;
import com.dhiram.ecom_pro.utils.PasswordBCrypt;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

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
    public User createUser(@RequestBody User user) {
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
}
