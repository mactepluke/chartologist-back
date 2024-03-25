package co.syngleton.chartomancer.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

interface JWTHelper {

    String generateToken(String user);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);

    List<GrantedAuthority> getAuthoritiesFromToken(String token);


}
