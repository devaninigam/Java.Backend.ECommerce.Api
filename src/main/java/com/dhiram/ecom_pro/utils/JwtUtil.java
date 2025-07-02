
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

    private final String SECRET_KEY = "YourSecretKeyMustBeLongEnoughToBeSecureNigam123!AndEvenLongerForProductionUse";
    private final long DEFAULT_EXPIRATION_TIME = 1000000000000L; // 1 day

    // Expiration time for the JWT token (e.g., 1 day in milliseconds)
    // 1000L * 60 * 60 * 24 = 86,400,000 milliseconds (1 day)
    // private final long EXPIRATION_TIME = 86400000L;

    // Generates a secure key from the SECRET_KEY string
    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email, List<String> roles, long expirationTime) {
        long actualExpiration = expirationTime > 0 ? expirationTime : DEFAULT_EXPIRATION_TIME;
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + actualExpiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT token.
     * 
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
     * 
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
     * 
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
     * 
     * @return The expiration time in milliseconds.
     */
    public long getEXPIRATION_TIME() {
        return DEFAULT_EXPIRATION_TIME;
    }
}
