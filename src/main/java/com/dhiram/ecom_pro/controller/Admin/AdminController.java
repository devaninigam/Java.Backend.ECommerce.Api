package com.dhiram.ecom_pro.controller.Admin;

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
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hello")
    public String testConnection() {
        return "/api/admin/hello";
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

        List<String> roles = List.of("ROLE_ADMIN");

        sentData.put("status", "Admin Login successful");
        sentData.put("email", foundUser.get().getEmail());
        sentData.put("name", foundUser.get().getName());
        sentData.put("id", foundUser.get().getId());
        sentData.put("token", jwtUtil.generateToken(email, roles, 0));

        return new ResponseEntity<>(sentData, HttpStatus.OK);
    }

}
