package ua.opnu.bankist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ua.opnu.bankist.service.CustomUserDetailsService;

import java.util.List;

@Configuration // Annotation indicating that this class is used for Spring configuration
@EnableWebSecurity // Enables Spring Security's web security support
public class SecurityConfig {

    // Bean definition for UserDetailsService, which provides user information
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    // Bean definition for PasswordEncoder, which encodes passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Main security configuration method
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://127.0.0.1:5500")); // Allowed origins
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed methods
                    corsConfiguration.setAllowedHeaders(List.of("*")); // Allowed headers
                    corsConfiguration.setAllowCredentials(true); // Allow credentials
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/validateCredentials", "/api/users/exists", "/api/users").permitAll() // Public endpoints
                        .requestMatchers("/api/cards/**", "/api/loans/**", "/api/transactions/**").permitAll() // Public endpoints
                        .anyRequest().permitAll() // All other requests are allowed
                )
                .formLogin(form -> form
                        .loginPage("http://127.0.0.1:5500/src/pages/login/login.html").permitAll() // Login page
                        .loginProcessingUrl("/perform_login") // URL to submit login credentials
                        .defaultSuccessUrl("http://127.0.0.1:5500/src/pages/dashboard/dashboard.html", true) // Success URL
                        .failureUrl("http://127.0.0.1:5500/src/pages/login/login.html?error=true") // Failure URL
                )
                .logout(logout -> logout
                        .logoutUrl("/perform_logout") // Logout URL
                        .logoutSuccessUrl("http://127.0.0.1:5500/src/pages/login/login.html").permitAll() // Logout success URL
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Session creation policy
                )
                .sessionManagement(session -> session
                        .invalidSessionUrl("http://127.0.0.1:5500/src/pages/login/login.html") // URL for invalid session
                        .maximumSessions(1) // Maximum concurrent sessions
                )
                .requestCache(requestCache -> requestCache.disable()); // Disable request cache

        return http.build();
    }

    // Bean definition for configuring HTTP Firewall
    @Bean
    public HttpFirewall allowSemicolonHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // Allow semicolon in URL
        return firewall;
    }

    // Bean definition for CORS filter
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://127.0.0.1:5500"); // Allowed origin
        config.addAllowedHeader("*"); // Allowed headers
        config.addAllowedMethod("*"); // Allowed methods
        source.registerCorsConfiguration("/**", config); // Register CORS configuration
        return new CorsFilter(source);
    }
}