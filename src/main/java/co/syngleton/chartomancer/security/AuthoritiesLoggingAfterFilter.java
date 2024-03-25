package co.syngleton.chartomancer.security;

import jakarta.servlet.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Log4j2
class AuthoritiesLoggingAfterFilter implements Filter  {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            log.info("User " + authentication.getName() + " is successfully authenticated and "
                    + "has the authorities " + authentication.getAuthorities().toString());
        } else {
            log.info("User has not been authenticated.");
        }
        chain.doFilter(request, response);
    }
}
