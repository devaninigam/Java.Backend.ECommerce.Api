package com.dhiram.ecom_pro.repo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dhiram.ecom_pro.model.ProductModel;

import jakarta.transaction.Transactional;

@Repository
public interface ProductRepo extends JpaRepository<ProductModel, UUID> {

    @Query("""
        SELECT p FROM ProductModel p 
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<ProductModel> searchProducts(String keyword);

    @Transactional
    List<ProductModel> findByUserId(@Param("userId") UUID userId);
}
