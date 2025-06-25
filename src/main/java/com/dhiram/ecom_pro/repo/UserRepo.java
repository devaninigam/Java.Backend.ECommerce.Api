package com.dhiram.ecom_pro.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dhiram.ecom_pro.model.User;

public interface UserRepo extends JpaRepository<User, UUID> {

    public Optional<User> findByEmail(String email);
    
}
