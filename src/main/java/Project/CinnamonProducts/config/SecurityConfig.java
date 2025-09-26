package Project.CinnamonProducts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .authorizeHttpRequests(authz -> authz
                // Allow public access to payment and checkout endpoints
                .requestMatchers("/api/payment/**", "/api/checkout/**", "/api/cart/**").permitAll()
                // Allow access to health check and actuator endpoints
                .requestMatchers("/actuator/**", "/api/payment/health").permitAll()
                // Allow access to static resources
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.disable()) // Disable basic auth
            .formLogin(formLogin -> formLogin.disable()); // Disable form login
        
        return http.build();
    }
}
