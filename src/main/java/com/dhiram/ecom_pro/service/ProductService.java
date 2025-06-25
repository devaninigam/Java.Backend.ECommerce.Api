package com.dhiram.ecom_pro.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dhiram.ecom_pro.model.ProductModel;
import com.dhiram.ecom_pro.repo.ProductRepo;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserService userService;

    public List<ProductModel> getAllProducts() {
        return productRepo.findAll();
    }

    public ProductModel getProductsById(UUID id) {
        return productRepo.findById(id).orElse(null);
    }

    public ProductModel addProducts(ProductModel prod, MultipartFile imageFile) throws IOException {
        if (userService.getUserById(prod.getUserId()) != null) {
            prod.setImageName(imageFile.getOriginalFilename());
            prod.setImageType(imageFile.getContentType());
            prod.setImageData(imageFile.getBytes());
            return productRepo.save(prod);
        } else {
            return null;
        }
    }

    public ProductModel updateProducts(UUID id, ProductModel prod, MultipartFile imageFile) throws IOException {
        if (userService.getUserById(prod.getUserId()) != null) {
            prod.setImageName(imageFile.getOriginalFilename());
            prod.setImageType(imageFile.getContentType());
            prod.setImageData(imageFile.getBytes());
            return productRepo.save(prod);
        } else {
            return null;
        }
    }

    public boolean deleteProducts(UUID id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<ProductModel> searchProducts(String keyword) {
        return productRepo.searchProducts(keyword);
    }

    public List<ProductModel> getProductsByUserId(UUID userId) {
        return productRepo.findByUserId(userId);
    }
}
