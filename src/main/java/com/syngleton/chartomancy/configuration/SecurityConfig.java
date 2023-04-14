package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.data.GenericData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Log4j2
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
    @Value("${role}")
    private String role;
    @Value("${devtools_email}")
    private String devToolsEmail;
    @Value("${devtools_password}")
    private String devToolsPassword;

    @Value("#{'${web.cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;
    @Value("#{'${web.cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;
    @Value("#{'${web.cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .anyRequest()
                        .authenticated()
                )
                .httpBasic(withDefaults())
                .csrf().disable();
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder().encode(password))
                .roles(role)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public com.syngleton.chartomancy.model.User devToolsUser()   {

        com.syngleton.chartomancy.model.User devToolsUser = new com.syngleton.chartomancy.model.User();

        devToolsUser.setEmail(devToolsEmail);
        devToolsUser.setPassword(passwordEncoder().encode(devToolsPassword));
        devToolsUser.setGenericData(new GenericData());

        return devToolsUser;
    }
}
