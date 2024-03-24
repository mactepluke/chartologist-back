package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.user_management.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

interface JWTHandler {

    String generateToken(String user);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);

    List<GrantedAuthority> getAuthoritiesFromToken(String token);


}
