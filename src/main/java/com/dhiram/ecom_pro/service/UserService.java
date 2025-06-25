package com.dhiram.ecom_pro.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhiram.ecom_pro.model.User;
import com.dhiram.ecom_pro.repo.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User getUserById(UUID id) {
        return userRepo.findById(id).orElse(null);
    }

}
