package com.transportes.transporte; // <-- ¡El paquete de este microservicio!

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate; // <-- (Este import lo agregamos antes)

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // --- ¡ESTA ES LA LÍNEA CLAVE! ---
            // Le decimos que deshabilite la protección CSRF
            .csrf(csrf -> csrf.disable())

            // Y ya que estamos, le decimos que permita todas las peticiones
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            );

        return http.build();
    }

    // (Este es el bean de RestTemplate que ya tenías que crear)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}