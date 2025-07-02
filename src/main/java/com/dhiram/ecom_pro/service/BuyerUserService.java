package com.dhiram.ecom_pro.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dhiram.ecom_pro.dto.EmailResendRequest;
import com.dhiram.ecom_pro.dto.LoginRequest;
import com.dhiram.ecom_pro.dto.RESETPassword;
import com.dhiram.ecom_pro.model.BuyerUser;
import com.dhiram.ecom_pro.model.OtpVerificationRequest;
import com.dhiram.ecom_pro.repo.BuyerRepo;
import com.dhiram.ecom_pro.utils.JwtUtil;
import com.dhiram.ecom_pro.utils.OtpUtil;
import com.dhiram.ecom_pro.utils.PasswordBCrypt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BuyerUserService {

    @Autowired
    private BuyerRepo buyerRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    public BuyerUser registrationBuyerUser(BuyerUser buyerUser) {
        if (buyerRepo.existsByEmail(buyerUser.getEmail())) {
            return null;
        }
        buyerUser.setLoginVerificationTimestamp(OtpUtil.calculateExpiryTime());
        buyerUser.setLoginVerificationCode(OtpUtil.generateOtp());
        boolean emailSent = emailService.sendSimpleEmail(
                buyerUser.getEmail(),
                "OTP Verification",
                "Your OTP is: " + buyerUser.getLoginVerificationCode());
        if (!emailSent) {
            System.err.println("Warning: OTP email failed to send, but user was still registered");
        }
        buyerUser.setPassword(PasswordBCrypt.hashPassword(buyerUser.getPassword()));
        return buyerRepo.save(buyerUser);
    }

    public ResponseEntity<?> otpVerificationService(OtpVerificationRequest otpVerificationRequest) {
        BuyerUser buyerUser = buyerRepo.findByEmail(otpVerificationRequest.getEmail());
        if (buyerUser == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message", "User not found",
                            "status", "error",
                            "errorDetails", "No user found with the provided email: "
                                    + otpVerificationRequest.getEmail()));
        }

        if (!otpVerificationRequest.getOtp().equals(buyerUser.getLoginVerificationCode())) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message", "Invalid OTP",
                            "status", "error",
                            "errorDetails",
                            "The provided OTP does not match the user's OTP"));
        }

        if (OtpUtil.isOtpExpired(buyerUser.getLoginVerificationTimestamp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "OTP has expired",
                            "status", "error",
                            "errorDetails",
                            "The OTP has expired. Please request a new OTP."));
        }

        buyerUser.setLoginVerificationCode(null);
        buyerUser.setLoginVerificationTimestamp(null);
        buyerUser.setLoginVerification(true);
        buyerRepo.save(buyerUser);

        return ResponseEntity.ok()
                .body(Map.of(
                        "message", "OTP verified successfully",
                        "status", "success"));

    }

    // resendOtpService method to resend OTP
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> resendOtpService(String email) {
        log.info("Resending OTP for user: {}", email);
        BuyerUser buyerUser = buyerRepo.findByEmail(email);
        if (buyerUser == null) {
            log.warn("OTP resend failed - user not found: {}", email);
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message", "User not found",
                            "status", "error",
                            "errorDetails",
                            "No user found with the provided email: " + email));
        }

        if (buyerUser.getLoginVerification()) {
            log.warn("OTP resend blocked - user already verified: {}", email);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", "User already verified",
                            "status", "error",
                            "errorDetails", "Account is already active"));
        }

        buyerUser.setLoginVerificationTimestamp(OtpUtil.calculateExpiryTime());
        buyerUser.setLoginVerificationCode(OtpUtil.generateOtp());
        buyerRepo.save(buyerUser);

        try {
            boolean emailSent = emailService.sendSimpleEmail(
                    email,
                    "New OTP Verification",
                    "Your new OTP is: " + buyerUser.getLoginVerificationCode());

            if (!emailSent) {
                throw new RuntimeException("Failed to send OTP email");
            }

            log.info("OTP successfully resent to: {}", email);
            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "New OTP sent successfully",
                            "status", "success"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Failed to resend OTP",
                            "status", "error",
                            "errorDetails", "Email service unavailable"));

        }
    }

    public ResponseEntity<?> buyerUserlogin(LoginRequest loginRequest) {
        BuyerUser buyerUser = buyerRepo.findByEmail(loginRequest.getEmail());
        // Check if the user exists
        if (buyerUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Invalid credentials",
                            "status", "error",
                            "errorDetails", "No user found with the provided email: "
                                    + loginRequest.getEmail()));
        }
        if (buyerUser.getLoginVerification() == null || !buyerUser.getLoginVerification()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "User not verified",
                            "status", "error",
                            "errorDetails",
                            "Please verify your account before logging in"));
        }
        if (!PasswordBCrypt.matchesPassword(loginRequest.getPassword(), buyerUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid credentials",
                            "status", "error",
                            "errorDetails",
                            "Incorrect password for user: " + loginRequest.getEmail()));
        }

        List<String> roles = List.of("ROLE_BUYER");

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "data", Map.of(
                                "userId", buyerUser.getId(),
                                "email", buyerUser.getEmail(),
                                "name", buyerUser.getName(),
                                "token",
                                jwtUtil.generateToken(buyerUser.getEmail(), roles, 0)),
                        "message", "Login successful",
                        "status", "success"));
    }

    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> buyerUserForgotPassword(EmailResendRequest emailResponse) {
        String email = emailResponse.getEmail();
        BuyerUser buyerUser = buyerRepo.findByEmail(email);

        if (buyerUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Account not found",
                            "status", "error",
                            "details", "No account exists with email: " + email));
        }

        List<String> roles = List.of("ROLE_BUYER");
        try {
            String resetToken = jwtUtil.generateToken(buyerUser.getEmail(), roles, 300_000L);
            boolean emailSent = emailService.sendSimpleEmail(
                    email,
                    "Password Reset Instructions",
                    "Please click the following link to reset your password: " + resetToken +
                            "\n\nThis link will expire in 5 minute.");
            if (!emailSent) {
                throw new RuntimeException("Failed to send password reset email");
            }
            log.info("Password reset link sent successfully to: {}", email);

            buyerUser.setForgotPassword(true);
            buyerRepo.save(buyerUser);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "message", "Password reset link sent",
                            "status", "success",
                            "details", "Please check your email for the password reset link"));

        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Password reset failed",
                            "status", "error",
                            "details", "We couldn't send the password reset email. Please try again later."));
        }

    }

    public ResponseEntity<?> buyerUserResetPassword(RESETPassword resetPasswordResponse) {
        String token = resetPasswordResponse.getToken();
        String password = resetPasswordResponse.getPassword();
        String confirmPassword = resetPasswordResponse.getConfirmPassword();

        boolean tokenVerification = jwtUtil.validateToken(token);
        String tokenOnEmail = jwtUtil.extractUsername(token);

        if (!tokenVerification) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", "error",
                            "message", "Password reset token has expired or is invalid"));
        }

        BuyerUser buyerUser = buyerRepo.findByEmail(tokenOnEmail);

        if (buyerUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Invalid Token",
                            "status", "error"));
        }

        if (!buyerUser.getForgotPassword()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Password reset not requested",
                            "status", "error"));
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", "error",
                            "message", "Password and confirmation password do not match"));
        }
        buyerUser.setPassword(password);
        buyerUser.setForgotPassword(false);
        buyerRepo.save(buyerUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", "success",
                        "message", "Password updated successfully"));
    }
}
