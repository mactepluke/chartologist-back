package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.user_management.Role;
import co.syngleton.chartomancer.user_management.User;
import lombok.AllArgsConstructor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DefaultJWTHandler implements JWTHandler {
    private Key key;
    private String expiration;

    @Override
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    @Override
    public LocalDateTime getExpirationDateFromToken(String token) {
        return toLocalDateTime(getAllClaimsFromToken(token).getExpiration());
    }

    @Override
    public String generateToken(User user) {
        Map<String, List<GrantedAuthority>> claims = new HashMap<>();
        claims.put("role", List.of(Role.ROLE_USER));
        return doGenerateToken(claims, user.getUsername());
    }

    private String doGenerateToken(Map<String, List<GrantedAuthority>> claims, String username) {
        long expirationTimeInSeconds = Long.parseLong(expiration);
        final LocalDateTime createdDateTime = LocalDateTime.now();
        final LocalDateTime expirationDateTime = createdDateTime.plusSeconds(expirationTimeInSeconds);

        return Jwts.builder()
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
}
