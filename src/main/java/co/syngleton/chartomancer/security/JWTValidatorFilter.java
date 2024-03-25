package co.syngleton.chartomancer.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

class JWTValidatorFilter extends OncePerRequestFilter {
    private final JWTHelper jwtHelper;

    JWTValidatorFilter(JWTHelper jwtHelper) {
        super();
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        /*We check for the Token in any requests not part of the login process, but don't throw an exception if it's not there
        even if the token is invalid because some endpoints can still be accessible. We only want to give the AuthenticationProvider
        the information it needs to know when the .authenticated() endpoints are accessed.
         */
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwt != null && jwtHelper.validateToken(jwt)) {

            Authentication auth = new UsernamePasswordAuthenticationToken(jwtHelper.getUsernameFromToken(jwt), null,
                    jwtHelper.getAuthoritiesFromToken(jwt));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            //We want to prevent access to the endpoints that require authentication if the token is invalid even if a Basic auth is provided
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/user/login");
    }



}
