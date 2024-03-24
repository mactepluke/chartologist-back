package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.user_management.User;
import io.jsonwebtoken.Claims;

import java.time.LocalDateTime;

public interface JWTHandler {
    Claims getAllClaimsFromToken(String token);

    String getUsernameFromToken(String token);

    LocalDateTime getExpirationDateFromToken(String token);

    String generateToken(User user);

    boolean validateToken(String token);
}
