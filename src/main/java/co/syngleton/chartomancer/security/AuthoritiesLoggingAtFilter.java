package co.syngleton.chartomancer.security;

import jakarta.servlet.*;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
class AuthoritiesLoggingAtFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Authentication Validation is in progress.");
        chain.doFilter(request, response);
    }
}
