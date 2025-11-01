package me.asreal.markgenius.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity //Enables web securit support
@AllArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /// Diables CSF protection and authorizes all http request for the auth header requiring auth from all other sources.
    /// Stateless policy session requires all request to be treated as a new request even from an authenticated client
    @Bean
    public SecurityFilterChain springSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //Security configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsConfigurationSource = new CorsConfiguration();
        var source = new UrlBasedCorsConfigurationSource();
        //TODO: Add authorized host
        corsConfigurationSource.setAllowedOrigins(List.of("http://localhost:8080"));
        corsConfigurationSource.setAllowedMethods(List.of("GET",  "POST", "PUT", "DELETE"));
        corsConfigurationSource.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        //Manage source
        source.registerCorsConfiguration("/**", corsConfigurationSource);
        return source;
    }

}
