package co.syngleton.chartomancer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
class JWTGeneratorFilter extends OncePerRequestFilter {
    private final JWTHelper jwtHelper;

    JWTGeneratorFilter(JWTHelper jwtHelper) {
        super();
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        /* We create the token in the header even if we did not authenticate the user, but remove it from the response*
        if required by invoking exceptionHandling(...).authenticationEntryPoint to handle exceptions thrown
        by the AuthenticationProvider at the end of the authentication process.
         */
        if (authentication != null) {
            response.setHeader(HttpHeaders.AUTHORIZATION, jwtHelper.generateToken(authentication.getName()));
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/user/login");
    }

}
