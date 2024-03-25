package co.syngleton.chartomancer.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import io.jsonwebtoken.security.Keys;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Log4j2
@AllArgsConstructor
@Configuration
@EnableWebSecurity
class SecurityConfig {
    private final WebProperties wp;

    @Bean
    public JWTHelper jwtHandler() {
        return new DefaultJWTHelper(Keys.hmacShaKeyFor(wp.jjwtSecret().getBytes()), wp.jjwtExpiration());
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setHeader(HttpHeaders.AUTHORIZATION, null);
                            /*We need to suppress with header in order to avoid the browser to show the default login popup
                            when wrong credentials are entered and a response is made.*/
                            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "None");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/config/**").permitAll()
                        .requestMatchers("/user/create").permitAll()
                        .requestMatchers("/backtesting/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new ApiKeyAuthenticationFilter(wp.backendApiKey()), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTGeneratorFilter(jwtHandler()), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTValidatorFilter(jwtHandler()), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(wp.corsAllowedOrigins());
        configuration.setAllowedMethods(wp.corsAllowedMethods());
        configuration.setAllowedHeaders(wp.corsAllowedHeaders());
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
