package com.dhiram.ecom_pro.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dhiram.ecom_pro.dto.DeleteCartsRequest;
import com.dhiram.ecom_pro.dto.PostAddCartsRequest;
import com.dhiram.ecom_pro.model.BuyerUser;
import com.dhiram.ecom_pro.model.BuyerUser.ProductHistoryEntry;
import com.dhiram.ecom_pro.model.ProductModel;
import com.dhiram.ecom_pro.repo.BuyerRepo;
import com.dhiram.ecom_pro.repo.ProductRepo;


@Service
public class BuyerUserProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private BuyerRepo buyerRepo;

    public ResponseEntity<?> getAllProducts() {
        List<ProductModel> products = productRepo.findAll();
        List<Map<String, Object>> productList = new ArrayList<>();

        for (ProductModel product : products) {
            productList.add(convertProductToMap(product));
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
        BuyerUser buyerUser = buyerRepo.findById(postAddCartsRequest.getBuyerUserId()).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Product not found",
                    "status", "error"
            ));
        }

        if (buyerUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "User not found",
                    "status", "error"
            ));
        }

        for (ProductHistoryEntry productHistory : buyerUser.getProductHistory()) {
            if (productHistory.getProductId().equals(postAddCartsRequest.getProductId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "message", "Product already exists in cart",
                        "status", "error",
                        "details", "Product with ID " + postAddCartsRequest.getProductId() + " is already in the user's cart"
                ));
            }
        }

        if (!product.isAvailable()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Product is not available",
                    "status", "error"
            ));
        } else if (product.getQuantity() < postAddCartsRequest.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Product quantity is not enough, product has only " + product.getQuantity() + " quantity",
                    "status", "error"
            ));
        }

        buyerUser.getProductHistory().add(new ProductHistoryEntry(
                postAddCartsRequest.getProductId(),
                postAddCartsRequest.getQuantity()
        ));

        buyerRepo.save(buyerUser);
        return ResponseEntity.ok(Map.of(
                "message", "Product added to cart successfully",
                "status", "success"
        ));

    }

    public ResponseEntity<?> putUpdateCarts(PostAddCartsRequest postAddCartsRequest) {
        ProductModel product = productRepo.findById(postAddCartsRequest.getProductId()).orElse(null);
        BuyerUser buyerUser = buyerRepo.findById(postAddCartsRequest.getBuyerUserId()).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Product not found",
                    "status", "error"
            ));
        }

        if (buyerUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "User not found",
                    "status", "error"
            ));
        }

        int cartIndex = -1;
        int i = 0;

        for (ProductHistoryEntry productHistory : buyerUser.getProductHistory()) {
            if (productHistory.getProductId().equals(postAddCartsRequest.getProductId())) {
                cartIndex = i;
                break;
            }
            i++;
        }

        if (!product.isAvailable()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Cannot update cart - product is currently unavailable",
                    "status", "error"
            ));
        } else if (product.getQuantity() < postAddCartsRequest.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "code", "INSUFFICIENT_STOCK",
                    "message", "Insufficient product quantity",
                    "available", product.getQuantity(),
                    "requested", postAddCartsRequest.getQuantity(),
                    "productId", product.getId()
            ));
        }

        ProductHistoryEntry updatedEntry = new ProductHistoryEntry(
                postAddCartsRequest.getProductId(),
                postAddCartsRequest.getQuantity()
        );

        buyerUser.getProductHistory().set(cartIndex, updatedEntry);
        buyerRepo.save(buyerUser);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Cart updated successfully",
                "data", Map.of(
                        "productId", postAddCartsRequest.getProductId(),
                        "newQuantity", postAddCartsRequest.getQuantity()
                )
        ));

    }

    public ResponseEntity<?> getAllCarts(UUID buyerId) {
        BuyerUser buyerUser = buyerRepo.findById(buyerId).orElse(null);

        if (buyerUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "User not found",
                    "status", "error"
            ));
        }

        if (buyerUser.getProductHistory().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "No cart history found for this user",
                    "status", "error",
                    "details", "The user exists, but no products have been added to the cart yet"
            ));
        }

        List<Map<String, Object>> productHistoryList = new ArrayList<>();

        for (ProductHistoryEntry productHistory : buyerUser.getProductHistory()) {
            ProductModel product = productRepo.findById(productHistory.getProductId()).orElse(null);
            Map<String, Object> productHistoryData = new HashMap<>();
            productHistoryData.put("product", convertProductToMap(product));
            productHistoryData.put("orderQuntity", productHistory.getQuantity());
            productHistoryList.add(productHistoryData);
        }

        return ResponseEntity.ok().body(Map.of(
                "data", productHistoryList,
                "message", "Cart retrieved successfully",
                "status", "success",
                "count", productHistoryList.size()
        ));
    }

    private Map<String, Object> convertProductToMap(ProductModel product) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("description", product.getDescription());
        map.put("brand", product.getBrand());
        map.put("price", product.getPrice());
        map.put("category", product.getCategory());
        map.put("releaseDate", product.getReleaseDate());
        map.put("available", product.isAvailable());
        map.put("quantity", product.getQuantity());
        map.put("imageData", product.getImageData());
        return map;
    }

  public ResponseEntity<?> deleteCarts(DeleteCartsRequest deleteCartsRequest) {
    BuyerUser buyerUser = buyerRepo.findById(deleteCartsRequest.getBuyerUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    int index = -1;
    for (int i = 0; i < buyerUser.getProductHistory().size(); i++) {
        if (buyerUser.getProductHistory().get(i).getProductId().equals(deleteCartsRequest.getProductId())) {
            index = i;
            break;
        }
    }

    // 3. Remove product from cart
    if (index == -1) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "status", "error",
            "code", "PRODUCT_NOT_IN_CART",
            "message", "Product not found in user's cart",
            "details", Map.of(
                "productId", deleteCartsRequest.getProductId(),
                "userId", deleteCartsRequest.getBuyerUserId()
            )
        ));
    }

    // 4. Remove product from cart
    ProductHistoryEntry removedItem = buyerUser.getProductHistory().remove(index);
    buyerRepo.save(buyerUser);

    return ResponseEntity.ok(Map.of(
        "status", "success",
        "code", "ITEM_REMOVED",
        "message", "Product successfully removed from cart",
        "data", Map.of(
            "removedProductId", removedItem.getProductId(),
            "removedQuantity", removedItem.getQuantity(),
            "remainingItems", buyerUser.getProductHistory().size()
        )
    ));
}
}
