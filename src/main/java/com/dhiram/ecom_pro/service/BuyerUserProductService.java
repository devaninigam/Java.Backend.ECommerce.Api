package com.dhiram.ecom_pro.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dhiram.ecom_pro.dto.PostAddCartsRequest;
import com.dhiram.ecom_pro.model.ProductModel;
import com.dhiram.ecom_pro.repo.ProductRepo;

@Service
public class BuyerUserProductService {

    @Autowired
    private ProductRepo productRepo;

    public ResponseEntity<?> getAllProducts() {
        List<ProductModel> products = productRepo.findAll();
        List<Map<String, Object>> productList = new ArrayList<>();

        for (ProductModel product : products) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("id", product.getId());
            productData.put("name", product.getName());
            productData.put("description", product.getDescription());
            productData.put("brand", product.getBrand());
            productData.put("price", product.getPrice());
            productData.put("category", product.getCategory());
            productData.put("releaseDate", product.getReleaseDate());
            productData.put("available", product.isAvailable());
            productData.put("quantity", product.getQuantity());
            productData.put("imageData", product.getImageData());
            productList.add(productData);
        }

        if (productList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "message", "No products found",
                    "data", List.of(),
                    "count", 0
            ));

        }
        return ResponseEntity.ok(Map.of(
                "message", "Products fetched successfully",
                "data", productList,
                "count", productList.size()
        ));
    }

    public ResponseEntity<?> postAddCarts(PostAddCartsRequest postAddCartsRequest) {
        ProductModel product = productRepo.findById(postAddCartsRequest.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Product not found",
                    "status", "error"
            ));
        }
        return null;
    }
}
