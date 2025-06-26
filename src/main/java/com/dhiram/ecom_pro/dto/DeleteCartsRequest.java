package com.dhiram.ecom_pro.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteCartsRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Buyer User ID is required")
    private UUID buyerUserId;
}
