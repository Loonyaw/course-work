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
import ua.opnu.bankist.service.CustomUserDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://127.0.0.1:5500"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/exists").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/cards/**").permitAll()
                        .requestMatchers("/api/loans/**").permitAll()
                        .requestMatchers("/api/transactions/**").permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/src/pages/login/login.html").permitAll()
                        .loginProcessingUrl("/perform_login")
                        .defaultSuccessUrl("/src/pages/dashboard/dashboard.html", true)
                        .failureUrl("/src/pages/login/login.html?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/src/pages/login/login.html").permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
}
