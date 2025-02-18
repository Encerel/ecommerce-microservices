package by.innowise.orderservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;

@TestConfiguration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(GET, "/api/orders/my").hasRole("USER")
                .requestMatchers(PATCH, "/api/orders/{orderId}/cancel").hasRole("USER")
                .requestMatchers(GET, "/api/orders").hasRole("ADMIN")
                .requestMatchers("/api/orders/*").hasRole("ADMIN")
                .anyRequest().authenticated()
        );
        return http.build();
    }
}
