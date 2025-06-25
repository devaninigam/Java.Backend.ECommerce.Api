package com.dhiram.ecom_pro.controller.BuyerUser;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                    "message", "Error fetching products: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/add-carts")
    public ResponseEntity<?> postAddCarts(@Valid @RequestBody PostAddCartsRequest postAddCartsRequest) {
        try {
            return buyerUserProductService.postAddCarts(postAddCartsRequest);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Error adding products to cart: " + e.getMessage()
            ));
        }
    }

}
