package com.dhiram.ecom_pro.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dhiram.ecom_pro.model.BuyerUser;

public interface BuyerRepo extends JpaRepository<BuyerUser, UUID> {
    
    boolean existsByEmail(String email);
    BuyerUser findByEmail(String email);
}
