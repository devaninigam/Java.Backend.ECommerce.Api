package com.dhiram.ecom_pro.controller.BuyerUser;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhiram.ecom_pro.dto.EmailResendRequest;
import com.dhiram.ecom_pro.dto.LoginRequest;
import com.dhiram.ecom_pro.dto.RESETPassword;
import com.dhiram.ecom_pro.model.BuyerUser;
import com.dhiram.ecom_pro.model.OtpVerificationRequest;
import com.dhiram.ecom_pro.response.ErrorResponse;
import com.dhiram.ecom_pro.service.BuyerUserService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/buyer-user")
public class BuyerUserController {

    @Autowired
    private BuyerUserService buyerUserService;

    @PostMapping("/registration")
    public ResponseEntity<?> registrationBuyerUser(@RequestBody BuyerUser buyerUserData) {
        try {
            BuyerUser buyerUserSaveNewData = buyerUserService.registrationBuyerUser(buyerUserData);
            if (buyerUserSaveNewData == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse(
                                "User with this email already exists",
                                "error",
                                LocalDateTime.now(),
                                "USER_ALREADY_EXISTS"));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "message",
                            "OTP has been sent successfully. Please check your email " + buyerUserSaveNewData.getEmail()
                                    + " to verify your account.",
                            "status", "success",
                            "timestamp", LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Registration failed: " + e.getMessage(),
                            "status", "error",
                            "timestamp", LocalDateTime.now()));
        }
    }

    @PostMapping("/varify-login-otp")
    public ResponseEntity<?> varifyLoginOtp(@Valid @RequestBody OtpVerificationRequest otpVerificationRequest) {
        try {
            ResponseEntity<?> buyerUser = buyerUserService.otpVerificationService(otpVerificationRequest);
            return buyerUser;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "OTP verification failed: " + e.getMessage(),
                            "status", "error",
                            "timestamp", LocalDateTime.now()));
        }
    }

    @PostMapping("/resend-login-otp")
    public ResponseEntity<?> resendLoginOtp(@Valid @RequestBody EmailResendRequest otpResendRequest) {
        try {
            ResponseEntity<?> buyerUser = buyerUserService.resendOtpService(otpResendRequest.getEmail());
            return buyerUser;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Resend OTP failed: " + e.getMessage(),
                            "status", "error",
                            "timestamp", LocalDateTime.now()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> buyerUserlogin(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            ResponseEntity<?> buyerUser = buyerUserService.buyerUserlogin(loginRequest);
            return buyerUser;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Login failed: " + e.getMessage(),
                            "status", "error",
                            "timestamp", LocalDateTime.now()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> buyerUserForgotPassword(@Valid @RequestBody EmailResendRequest emailResponse) {
        try {
            ResponseEntity<?> buyerUser = buyerUserService.buyerUserForgotPassword(emailResponse);
            return buyerUser;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to process password reset request",
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> buyerUserResetPassword(@Valid @RequestBody RESETPassword resetPasswordResponse) {
        try {
            ResponseEntity<?> buyerUser = buyerUserService.buyerUserResetPassword(resetPasswordResponse);
            return buyerUser;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "An unexpected error occurred while processing your request",
                            "details", "Please try again later or contact support",
                            "timestamp", LocalDateTime.now()));

        }
    }
}
