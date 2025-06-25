package com.dhiram.ecom_pro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "ecom_buyer_user")
public class BuyerUser {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Column(nullable = false)
    private String password;

    @Column(nullable = true, columnDefinition = "boolean default false")
    private Boolean forgotPassword = false;

    // For login is valid 
    @Column(nullable = true, columnDefinition = "boolean default false")
    private Boolean loginVerification = false;

    // For code set
    @Column(nullable = true)
    private String loginVerificationCode;

    // For Expair time 
    @Column(name = "login_verification_timestamp", nullable = true)
    private Instant loginVerificationTimestamp;

    @NotBlank(message = "Address is required")
    @Size(max = 150, message = "Address cannot exceed 150 characters")
    @Column(nullable = true)
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
    @Column(nullable = false)
    private String phone;

    @ElementCollection
    @CollectionTable(
            name = "buyer_product_history",
            joinColumns = @JoinColumn(name = "buyer_id")
    )
    private List<ProductHistoryEntry> productHistory = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductHistoryEntry {

        @Column(name = "product_id", nullable = false)
        private UUID productId;

        @Column(name = "quantity", nullable = false)
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

    }
}
