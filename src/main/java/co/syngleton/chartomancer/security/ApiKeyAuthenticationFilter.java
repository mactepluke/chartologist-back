package co.syngleton.chartomancer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private final String backendApiKey;

    ApiKeyAuthenticationFilter(String backendApiKey) {
        super();
        this.backendApiKey = backendApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (backendApiKey.equals(apiKey)) {
            filterChain.doFilter(request, response);
        } else {
            log.debug("API key mismatch, provided: {}, expected: {}", apiKey, backendApiKey);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
