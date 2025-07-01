package com.dhiram.ecom_pro.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dhiram.ecom_pro.utils.JwtUtil;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException { // Corrected IOException import
        String authHeader = request.getHeader("Authorization");
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7); // Extract the JWT token
            String username = null;
            List<String> roles = null;

            try {
                username = jwtUtil.extractUsername(jwt); // Extract username from JWT
                roles = jwtUtil.extractRoles(jwt); // Extract roles from JWT
            } catch (Exception e) {
                // Log the exception for debugging, but don't stop the filter chain
                System.err.println("Error extracting JWT details: " + e.getMessage());
                // Optionally, you could set a response status here if the token is invalid
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // return; // Stop processing this request if token is invalid
            }

            // If username is valid and no authentication is currently set in SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Validate the JWT token
                if (jwtUtil.validateToken(jwt)) {
                    // Convert roles (String list) to SimpleGrantedAuthority list
                    List<SimpleGrantedAuthority> authorities = (roles != null)
                            ? roles.stream().map(SimpleGrantedAuthority::new).toList()
                            : List.of();
                    
                    System.out.println("Extracted roles: " + roles);
                    System.out.println("Granted authorities: " + authorities);

                    // Create an authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    
                    // Set authentication details from the request
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("JWT token validation failed for username: " + username);
                }
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}