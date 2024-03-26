package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.user_management.Role;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

class DefaultJWTHelper implements JWTHelper {
    private final Key key;
    private final String expiration;

    DefaultJWTHelper(String secret, String expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    @Override
    public String generateToken(String username) {
        Map<String, List<GrantedAuthority>> claims = new HashMap<>();
        claims.put("role", List.of(Role.ROLE_USER));

        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, List<GrantedAuthority>> claims, String username) {
        long expirationTimeInSeconds = Long.parseLong(expiration);
        final LocalDateTime createdDateTime = LocalDateTime.now();
        final LocalDateTime expirationDateTime = createdDateTime.plusSeconds(expirationTimeInSeconds);

        return Jwts.builder()
                .setIssuer("Chartologist")
                .setSubject("JWT Token")
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(toDate(createdDateTime))
                .setExpiration(toDate(expirationDateTime))
                .signWith(key)
                .compact();
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        final LocalDateTime tokenExpiration = getExpirationDateFromToken(token);
        return tokenExpiration.isBefore(LocalDateTime.now());
    }

    private LocalDateTime getExpirationDateFromToken(String token) {

        Claims claims = getAllClaimsFromToken(token);

        if (claims == null) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.systemDefault());
        }

        return toLocalDateTime(claims.getExpiration());
    }

    @Override
    public String getUsernameFromToken(String token) {

        Claims claims = getAllClaimsFromToken(token);

        if (claims == null) {
            return "";
        }

        return claims.getSubject();
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {

        Claims claims = getAllClaimsFromToken(token);

        if (claims == null) {
            return Collections.emptyList();
        }

        List roleslist = claims.get("role", List.class);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (Object role : roleslist) {
            grantedAuthorities.add(Role.fromString((String) role));
        }
        return grantedAuthorities;
    }

}
