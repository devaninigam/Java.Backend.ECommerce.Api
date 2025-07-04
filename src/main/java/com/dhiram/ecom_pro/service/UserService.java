package com.dhiram.ecom_pro.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dhiram.ecom_pro.dto.ResetPasswordRequest;
import com.dhiram.ecom_pro.dto.UserRendPasswordRequest;
import com.dhiram.ecom_pro.exception.GlobalExceptionHandler.ErrorResponse;
import com.dhiram.ecom_pro.model.User;
import com.dhiram.ecom_pro.repo.UserRepo;
import com.dhiram.ecom_pro.utils.OtpUtil;
import static com.dhiram.ecom_pro.utils.PasswordBCrypt.hashPassword;

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
                    .body(new ErrorResponse("404", "User not found"));
        }

        if (!userData.getPhoneNo().equals(emailResponse.getPhoneNo())) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse(
                            "404", "Phone number doesn't match registered number"));
        }

        if (userData.getForgotPasswordOtpLastResend() != null && OtpUtil.isOtpResendBlocked(userData.getForgotPasswordOtpLastResend())) {
            userData.setForgotPasswordOtpCount(0);
            userRepo.save(userData);
        } else if (userData.getForgotPasswordOtpCount() >= 3) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", "3600")
                    .body(new ErrorResponse(
                            "OTP_ATTEMPTS_EXCEEDED",
                            "You've exceeded maximum OTP attempts. Please try again after 1 hour."));
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

    public ResponseEntity<?> useResetPassword(ResetPasswordRequest resetPasswordResponse) {

        User userData = userRepo.findByEmail(resetPasswordResponse.getEmail()).orElse(null);

        if (userData == null) {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("404", "User not found"));
        }

        if (!resetPasswordResponse.getPassword().equals(resetPasswordResponse.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(
                            "PASSWORD_MISMATCH",
                            "Password and confirmation password do not match. Please ensure both fields are identical."));
        }

        if (!userData.getForgotPasswordStatus()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(
                            "RESET_DISABLED",
                            "Password reset not allowed"));

        }

        if (!userData.getForgotPasswordOtp().equals(resetPasswordResponse.getOtp())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(
                            "INVALID_OTP",
                            "Incorrect OTP"));
        }

        userData.setPassword(hashPassword(resetPasswordResponse.getPassword()));
        userData.setForgotPasswordStatus(false);
        userData.setForgotPasswordOtp(null);
        userData.setForgotPasswordOtpCount(0);
        userData.setForgotPasswordOtpLastResend(null);

        userRepo.save(userData);

        return ResponseEntity.ok().body(Map.of(
                "status", "success",
                "message", "Your password has been successfully updated"));
    }

}
