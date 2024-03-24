package co.syngleton.chartomancer.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

class JWTValidatorFilter extends OncePerRequestFilter {
    private final JWTHandler jwtHandler;

    JWTValidatorFilter(JWTHandler jwtHandler) {
        super();
        this.jwtHandler = jwtHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = request.getHeader("Authorization");

        if (jwt != null) {

            if (!jwtHandler.validateToken(jwt)) {
                throw new BadCredentialsException("Invalid JWT.");
            }

            jwtHandler.validateToken(jwt);

            Authentication auth = new UsernamePasswordAuthenticationToken(jwtHandler.getUsernameFromToken(jwt), null,
                    jwtHandler.getAuthoritiesFromToken(jwt));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/specific");
    }



}
