package com.dhiram.ecom_pro.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dhiram.ecom_pro.model.ProductModel;
import com.dhiram.ecom_pro.service.ProductService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService service;

    @RequestMapping("/")

    public String greet() {
        return "Hello Product";
    }

    @RequestMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(Map.of("data", service.getAllProducts(), "length", service.getAllProducts().size()));
    }

    @RequestMapping("/products/{id}")
    public ResponseEntity<?> getProductsById(@PathVariable UUID id
    ) {
        ProductModel product = service.getProductsById(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product with ID " + id + " not found.");
            response.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }
    }

    @PostMapping(value = {"/products", "/products/add"})
    public ResponseEntity<?> addProducts(
            @Valid @ModelAttribute ProductModel prod,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            ProductModel savedProduct = service.addProducts(prod, imageFile);
            if (savedProduct != null) {
                return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "User not found",
                                "userId", prod.getName(),
                                "timestamp", Instant.now()
                        ));
            }
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/{id}/image")
    public ResponseEntity<byte[]> getProductsImageById(@PathVariable UUID id) throws IOException {
        ProductModel product = service.getProductsById(id);
        byte[] imageFile = product.getImageData();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(product.getImageType()))
                .body(imageFile);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProducts(
            @PathVariable UUID id,
            @Valid @ModelAttribute ProductModel prod,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        ProductModel product;
        try {
            ProductModel findProduct = service.getProductsById(id);
            if (findProduct != null) {
                product = service.updateProducts(id, prod, imageFile);
                if (product != null) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(Map.of(
                                    "data", product,
                                    "message", "Product is successfully updated!",
                                    "timestamp", Instant.now()
                            ));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of(
                                    "error", "user_not_found",
                                    "message", "The requested user account could not be located",
                                    "userId", prod.getUserId().toString(),
                                    "timestamp", Instant.now()
                            ));
                }
            }
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Failed to update", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProducts(@PathVariable UUID id) {
        try {
            ProductModel findProduct = service.getProductsById(id);
            if (findProduct != null) {
                boolean productDeleted = service.deleteProducts(id);
                if (productDeleted) {
                    return ResponseEntity.ok(Map.of(
                            "message", "Product deleted successfully",
                            "status", "success"
                    ));
                } else {
                    return new ResponseEntity<>("Failed to Product Deleted" + id, HttpStatus.BAD_REQUEST);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message", "Product with ID " + id + " not found.",
                                "status", "Product not found"
                        ));
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = {"/products/{userId}", "/products/{userId}/users"})
    public ResponseEntity<?> getProductsByUserId(
            @PathVariable UUID userId) {
        List<ProductModel> products = service.getProductsByUserId(userId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Map<String, Object> sendData = new HashMap<>();
        sendData.put("data", products);
        sendData.put("length", products.size());
        return ResponseEntity.ok(sendData);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductModel>> searchProducts(@RequestParam String keyword) {
        List<ProductModel> products = service.searchProducts(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
