package co.syngleton.chartomancer.security;

import co.syngleton.chartomancer.user_management.Role;
import co.syngleton.chartomancer.user_management.User;
import co.syngleton.chartomancer.user_management.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        /*These are populated as part of filters like the JWT one, or from the 'Basic + encryption' classic "Authorization" header
        * that has been decoded by the default filter BasicAuthenticationFilter called by Spring at the beginning of the filterChain.*/
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        /* Regardless of the authentication method, we need to extract the username from the authentication object and verify if the user still exists.
        For example, the JWT token might be still valid but the user has been removed from the database.*/
        User user = userService.find(username);

        if (user == null) {
            throw new UsernameNotFoundException("User does not exist.");
        }
         // If the user has been authenticated by the filter responsible for JWT authentication, we can stop here.
        if (authentication.isAuthenticated())   {
            return authentication;
        }
        // If we did not stop at the previous step, this means that the user is trying to authenticate using a username and password as part of the /user/login request.
        if (!passwordEncoder.matches(password, user.getPassword()))  {
            throw new BadCredentialsException("Invalid password.");
        }

        return new UsernamePasswordAuthenticationToken(username, null, getGrantedAuthorities(user.getAuthorities()));
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
