package co.syngleton.chartomancer.security;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import io.jsonwebtoken.security.Keys;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Log4j2
@AllArgsConstructor
@Configuration
@EnableWebSecurity
class SecurityConfig {
    private final WebProperties wp;

    @Bean
    public JWTHandler jwtHandler() {
        return new DefaultJWTHandler(Keys.hmacShaKeyFor(wp.jjwtSecret().getBytes()), wp.jjwtExpiration());
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/config/**").permitAll()
                        //.requestMatchers("/user/login").permitAll()
                        .requestMatchers("/user/create").permitAll()
                        .requestMatchers("/backtesting/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAt(new AuthoritiesLoggingAtFilter(),BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new ApiKeyAuthenticationFilter(wp.backendApiKey()), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTGeneratorFilter(jwtHandler()), BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTValidatorFilter(jwtHandler()), BasicAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable);

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
