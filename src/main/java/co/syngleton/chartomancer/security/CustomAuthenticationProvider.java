package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.user_management.Role;
import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
@Log4j2
class CustomAuthenticationProvider implements AuthenticationProvider {
    private final JWTHandler jwtHandler;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        /*String authToken = authentication.getCredentials().toString();
        String username = jwtHandler.getUsernameFromToken(authToken);

        if (Boolean.FALSE.equals(jwtHandler.validateToken(authToken))) {
            throw new AuthenticationException("Invalid JWT.") {
            };
        }*/
        User user = userService.find(username);

        if (user == null) {
            throw new BadCredentialsException("User does not exist.");
        }

        if (!passwordEncoder.matches(password, user.getPassword()))  {
            throw new BadCredentialsException("Invalid password.");
        }
/*

        Claims claims = jwtHandler.getAllClaimsFromToken(authToken);
        List<GrantedAuthority> roles = claims.get("role", List.class);
*/

        return new UsernamePasswordAuthenticationToken(username, password, getGrantedAuthorities(user.getAuthorities()));
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
