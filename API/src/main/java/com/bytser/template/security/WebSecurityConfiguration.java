package be.pxl.student.birdwatching.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    // Allow user registration
                    .requestMatchers(HttpMethod.POST, "/users").permitAll()
                    // !Extra: Allow thirsty endpoint
                    .requestMatchers("/users/thirsty", "/users/thirsty/**").permitAll() 
                     // Allow health endpoints
                    .requestMatchers("/actuator/health/**", "/health/**").permitAll() 
                    // Allow Swagger / OpenAPI
                    .requestMatchers(
                        "/swagger", // <- main UI page (springdoc.swagger-ui.path=/swagger)
                        "/swagger-ui/**",       // static assets
                        "/api-docs/**"          // OpenAPI JSON (springdoc.api-docs.path=/api-docs)
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            // Stateless session
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}