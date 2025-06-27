package com.dhiram.ecom_pro.controller.BuyerUser;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhiram.ecom_pro.dto.DeleteCartsRequest;
import com.dhiram.ecom_pro.dto.PostAddCartsRequest;
import com.dhiram.ecom_pro.service.BuyerUserProductService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/buyer-user")
public class BuyerUserProductController {

    @Autowired
    private BuyerUserProductService buyerUserProductService;

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        try {
            return buyerUserProductService.getAllProducts();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching products: " + e.getMessage()));
        }
    }

    @PostMapping("/add-carts")
    public ResponseEntity<?> postAddCarts(@Valid @RequestBody PostAddCartsRequest postAddCartsRequest) {
        try {
            return buyerUserProductService.postAddCarts(postAddCartsRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error adding products to cart: " + e.getMessage()));
        }
    }

    @PutMapping("/update-carts")
    public ResponseEntity<?> putUpdateCarts(@Valid @RequestBody PostAddCartsRequest postAddCartsRequest) {
        try {
            return buyerUserProductService.putUpdateCarts(postAddCartsRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error adding products to cart: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-carts")
    public ResponseEntity<?> deleteCarts(@Valid @RequestBody DeleteCartsRequest deleteCartsRequest) {
        try {
            return buyerUserProductService.deleteCarts(deleteCartsRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error deleting products to cart: " + e.getMessage()));
        }
    }

    @GetMapping("/all-carts/{buyerId}")
    public ResponseEntity<?> getAllCarts(@PathVariable String buyerId) {
        try {
            if (buyerId == null || buyerId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "User id is required",
                        "status", "error"));
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(buyerId);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Invalid user ID format",
                        "status", "error"));
            }
            return buyerUserProductService.getAllCarts(uuid);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error fetching carts: " + e.getMessage()));
        }
    }
}
