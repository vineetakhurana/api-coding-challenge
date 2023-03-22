package com.synchrony.project.config;

import com.synchrony.project.exception.MyAuthenticationEntryPoint;
import com.synchrony.project.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        final String[] AUTH_WHITELIST = {
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**",
                "/v3/api-docs/**",
                "/api/public/**",
                "/api/public/authenticate",
                "/actuator/*",
                "/swagger-ui/**"
        };
        http
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
                .accessDeniedHandler(myAuthenticationEntryPoint)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(AUTH_WHITELIST)
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/admin")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/user")
                .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/myImages")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/image/*")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/image")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/image/*")
                .hasAnyRole("ADMIN", "USER")
                .and()
                .httpBasic();
        return http.csrf().disable().build();
    }
}
