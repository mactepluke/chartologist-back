package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.user_controller.CannotFindUserException;
import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserService;
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
public class JWTAuthenticationProvider implements AuthenticationProvider {
    private final JWTHandler jwtHandler;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String authToken = authentication.getCredentials().toString();
        String username = jwtHandler.getUsernameFromToken(authToken);

        if (Boolean.FALSE.equals(jwtHandler.validateToken(authToken))) {
            throw new AuthenticationException("Invalid JWT.") {
            };
        }
        if (userService.find(username) == null) {
            throw new AuthenticationException("User does not exist.") {
            };
        }

        Claims claims = jwtHandler.getAllClaimsFromToken(authToken);
        List<GrantedAuthority> roles = claims.get("role", List.class);

        return new UsernamePasswordAuthenticationToken(username, null, roles);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
