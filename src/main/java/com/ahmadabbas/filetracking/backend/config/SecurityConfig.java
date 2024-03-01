package com.ahmadabbas.filetracking.backend.config;

import com.ahmadabbas.filetracking.backend.auth.JwtAuthenticationFilter;
import com.ahmadabbas.filetracking.backend.exception.AuthEntryPoint;
import com.ahmadabbas.filetracking.backend.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;


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
//            "/api/v1/students/**",
//            "/api/v1/advisors/**",
//            "/api/v1/categories/**",
//            "/api/v1/documents/**"
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
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                // Adding new students or advisor
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/v1/students",
                                        "/api/v1/students/upload",
                                        "/api/v1/advisors"
                                ).hasRole(Role.ADMINISTRATOR.name())
                                // Modifying/changing category of a document
                                .requestMatchers("/api/v1/documents/modify-category")
                                .hasAnyRole(Role.ADMINISTRATOR.name(), Role.SECRETARY.name())
                                // Getting advisors and all students
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/advisors",
                                        "/api/v1/students",
                                        "/api/v1/students/"
                                )
                                .hasAnyRole(
                                        Role.ADMINISTRATOR.name(), Role.CHAIR.name(),
                                        Role.VICE_CHAR.name(), Role.SECRETARY.name(),
                                        Role.ADVISOR.name()
                                )
                                //
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "api/v1/documents/contact"
                                )
                                .hasRole(Role.STUDENT.name())
                                .anyRequest()
                                .authenticated()
                )
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

}
