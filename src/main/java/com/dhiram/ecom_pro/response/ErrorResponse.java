package com.dhiram.ecom_pro.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String status,
        LocalDateTime timestamp,
        String errorCode
        ) {

    public ErrorResponse(String message, String errorCode) {
        this(message, "error", LocalDateTime.now(), errorCode);
    }
}
