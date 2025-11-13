package com.operaciones.operacion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitamos CSRF (vital para que funcionen POST, PUT, DELETE)
            .csrf(csrf -> csrf.disable())
            
            // 2. Autorizamos todas las peticiones (permitAll)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permite cualquier petición sin autenticación
            );

        return http.build();
    }
}