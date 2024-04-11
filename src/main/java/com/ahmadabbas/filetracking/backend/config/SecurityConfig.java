package com.ahmadabbas.filetracking.backend.config;

import com.ahmadabbas.filetracking.backend.auth.JwtAuthenticationFilter;
import com.ahmadabbas.filetracking.backend.exception.AuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.ahmadabbas.filetracking.backend.user.Role.*;
import static org.springframework.http.HttpMethod.*;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] WHITE_LIST_URL = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/api/v1/auth/login",
            "/api/v1/auth/activate",
            "/api/v1/auth/activation-email",
    };

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthEntryPoint authEntryPoint;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(e -> e.authenticationEntryPoint(authEntryPoint))
                .authorizeHttpRequests(this::configureAuthorization)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(httpSecurityLogoutConfigurer ->
                        httpSecurityLogoutConfigurer.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }

    private void configureAuthorization(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry req
    ) {
        req.requestMatchers(WHITE_LIST_URL).permitAll()
                // Adding new students or advisor
                .requestMatchers(POST, "api/v1/students", "api/v1/students/upload", "api/v1/advisors")
                .hasRole(ADMINISTRATOR.name())
                // Modifying/changing category of a document
                .requestMatchers("api/v1/documents/modify-category")
                .hasAnyRole(ADMINISTRATOR.name(), SECRETARY.name())
                // Getting all advisors
                .requestMatchers(GET, "api/v1/advisors")
                .hasAnyRole(ADMINISTRATOR.name(), CHAIR.name(), SECRETARY.name())
                // Getting all students
                .requestMatchers(GET, "api/v1/students")
                .hasAnyRole(ADMINISTRATOR.name(), CHAIR.name(), SECRETARY.name(), ADVISOR.name())
                // Only students can add contact document
                .requestMatchers(POST, "api/v1/documents/upload/contact", "api/v1/documents/upload/petition", "api/v1/documents/upload/medical-report")
                .hasRole(STUDENT.name())
                // category creation
                .requestMatchers(POST, "api/v1/categories")
                .hasAnyRole(ADMINISTRATOR.name(), CHAIR.name(), SECRETARY.name())
                // only secretary and admin can upload
                .requestMatchers(POST, "api/v1/documents/upload", "api/v1/documents/upload/internship")
                .hasAnyRole(ADMINISTRATOR.name(), SECRETARY.name())
                // only admin can change category permissions them
                .requestMatchers(POST, "api/v1/categories/permissions/**")
                .hasRole(ADMINISTRATOR.name())
                // only admin or secretary can GET to all category perms
                .requestMatchers(GET, "api/v1/categories/permissions")
                .hasAnyRole(SECRETARY.name(), ADMINISTRATOR.name())
                // only admin can delete document
                .requestMatchers("api/v1/documents/*/delete")
                .hasRole(ADMINISTRATOR.name())
                // only admin can access users
                .requestMatchers("api/v1/users/**")
                .hasRole(ADMINISTRATOR.name())
                // only admin and secretary can update students
                .requestMatchers(PATCH, "api/v1/students/{studentId}")
                .hasAnyRole(ADMINISTRATOR.name(), SECRETARY.name())
                // only secretary can approve documents TODO: check later?
                .requestMatchers("api/v1/documents/*/approve")
                .hasRole(SECRETARY.name())

                .anyRequest().authenticated();
    }

}
