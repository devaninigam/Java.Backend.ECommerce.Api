
package com.dhiram.ecom_pro.utils;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // IMPORTANT: In a production environment, this key should be securely loaded
    // from environment variables or a secret management service, not hardcoded.
    // It should be at least 256 bits (32 characters for HS256)
    private final String SECRET_KEY = "YourSecretKeyMustBeLongEnoughToBeSecureNigam123!AndEvenLongerForProductionUse"; // Increased length for better security

    // Expiration time for the JWT token (e.g., 1 day in milliseconds)
    // 1000L * 60 * 60 * 24 = 86,400,000 milliseconds (1 day)
    private final long EXPIRATION_TIME = 86400000L; 

    // Generates a secure key from the SECRET_KEY string
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Generates a JWT token for a given email and list of roles.
     * @param email The subject of the token (e.g., user's email).
     * @param roles A list of roles (e.g., "ROLE_ADMIN", "ROLE_BUYER").
     * @return The generated JWT token string.
     */
    public String generateToken(String email, List<String> roles) {
        return Jwts.builder()
                .setSubject(email) // Sets the subject of the token
                .claim("roles", roles) // Adds custom claim for roles
                .setIssuedAt(new Date()) // Sets the token issuance date
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Sets the token expiration date
                .signWith(getKey(), SignatureAlgorithm.HS256) // Signs the token with the secret key and algorithm
                .compact(); // Builds and compacts the JWT into a string
    }

    /**
     * Extracts the username (subject) from a JWT token.
     * @param token The JWT token string.
     * @return The username extracted from the token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // Sets the signing key for parsing
                .build()
                .parseClaimsJws(token) // Parses the signed JWT
                .getBody() // Gets the claims body
                .getSubject(); // Extracts the subject (username)
    }

    /**
     * Extracts the roles from a JWT token.
     * @param token The JWT token string.
     * @return A list of roles extracted from the token.
     */
    @SuppressWarnings("unchecked") // Suppress warning for unchecked cast
    public List<String> extractRoles(String token) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class); // Extracts the "roles" claim as a List
    }

    /**
     * Validates a JWT token.
     * @param token The JWT token string.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true; // Token is valid
        } catch (JwtException e) {
            // Log the exception details for debugging purposes
            System.err.println("JWT Validation Error: " + e.getMessage());
            return false; // Token is invalid
        }
    }

    /**
     * Returns the configured expiration time for JWT tokens.
     * @return The expiration time in milliseconds.
     */
    public long getEXPIRATION_TIME() {
        return EXPIRATION_TIME;
    }
}
