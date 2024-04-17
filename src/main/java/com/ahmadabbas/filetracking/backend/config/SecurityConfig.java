package com.ahmadabbas.filetracking.backend.config;

import com.ahmadabbas.filetracking.backend.auth.JwtAuthenticationFilter;
import com.ahmadabbas.filetracking.backend.exception.AuthEntryPoint;
import com.ahmadabbas.filetracking.backend.util.AuthorizationUtils;
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


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
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
        req.requestMatchers(AuthorizationUtils.WHITE_LIST_URL).permitAll();
        for (var rule : AuthorizationUtils.getAuthorizationRules()) {
            if (rule.method() == null) {
                req.requestMatchers(rule.endpoints()).hasAnyRole(rule.roles());
            } else {
                req.requestMatchers(rule.method(), rule.endpoints()).hasAnyRole(rule.roles());
            }
        }
        req.anyRequest().authenticated();
    }

}
