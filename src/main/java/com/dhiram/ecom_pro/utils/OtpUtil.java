package com.dhiram.ecom_pro.utils;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();
    // private static final int OTP_LENGTH = 6; // 6-digit OTP
    private static final int OTP_VALID_MINUTES = 10;

    public static String generateOtp() {
        int otp = 100_000 + random.nextInt(900_00);
        return String.valueOf(otp);
    }

    public static Instant calculateExpiryTime() {
        return Instant.now().plus(OTP_VALID_MINUTES, ChronoUnit.MINUTES);
    }

    public static boolean isOtpExpired(Instant expiryTime) {
        return Instant.now().isAfter(expiryTime);
    }
}
