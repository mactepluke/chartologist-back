package co.syngleton.chartomancer.security;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ChartomancerAuthenticationProvider implements AuthenticationProvider {
    private final JWTUtil jwtUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String authToken = authentication.getCredentials().toString();
        String username = jwtUtil.getUsernameFromToken(authToken);

        if (Boolean.FALSE.equals(jwtUtil.validateToken(authToken))) {
            throw new AuthenticationException("Invalid token") {
            };
        }
        Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
        List<GrantedAuthority> roles = claims.get("role", List.class);

        return new UsernamePasswordAuthenticationToken(username, null, roles);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
