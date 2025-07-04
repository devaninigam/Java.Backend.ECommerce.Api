package com.dhiram.ecom_pro.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dhiram.ecom_pro.dto.UserRendPasswordRequest;
import com.dhiram.ecom_pro.exception.GlobalExceptionHandler;
import com.dhiram.ecom_pro.model.User;
import com.dhiram.ecom_pro.repo.UserRepo;
import com.dhiram.ecom_pro.utils.OtpUtil;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TwilioSmsService smsService;

    public User getUserById(UUID id) {
        return userRepo.findById(id).orElse(null);
    }

    public ResponseEntity<?> userForgotPassword(UserRendPasswordRequest emailResponse) {

        User userData = userRepo.findByEmail(emailResponse.getEmail()).orElse(null);

        if (userData == null) {
            return ResponseEntity.status(404)
                    .body(new GlobalExceptionHandler.ErrorResponse("404", "User not found"));
        }

        // if (!userData.getForgotPasswordStatus()) {
        // int statusCode = HttpStatus.FORBIDDEN.value();
        // return ResponseEntity.status(statusCode)
        // .body(new GlobalExceptionHandler.ErrorResponse("Password reset not allowed at
        // this time",
        // String.valueOf(statusCode)));
        // }

        if (!userData.getPhoneNo().equals(emailResponse.getPhoneNo())) {
            return ResponseEntity.status(404)
                    .body(new GlobalExceptionHandler.ErrorResponse(
                            "404", "Phone number doesn't match registered number"));
        }

        if (userData.getForgotPasswordOtpCount() >= 3) {
            if (!OtpUtil.isOtpResendBlocked(userData.getForgotPasswordOtpLastResend())) {
                // HTTP TOO_MANY_REQUESTS = 429
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .header("Retry-After", "3600")
                        .body(new GlobalExceptionHandler.ErrorResponse(
                                "429",
                                "You've exceeded the maximum OTP attempts. Please try again after 1 hour."));
            } else {
                userData.setForgotPasswordOtpCount(0);
            }
        }

        userData.setForgotPasswordStatus(true);
        userData.setForgotPasswordOtp(OtpUtil.generateOtp());
        userData.setForgotPasswordOtpCount(userData.getForgotPasswordOtpCount() + 1);
        userData.setForgotPasswordOtpLastResend(OtpUtil.calculateExpiryTime());

        smsService.sendSms(userData.getPhoneNo(), "you Opt is:- " + userData.getForgotPasswordOtp());
        userRepo.save(userData);
        return ResponseEntity.ok().body(Map.of(
                "status", "success",
                "message", "Password reset link sent successfully on:- " + userData.getPhoneNo()));
    }

}
