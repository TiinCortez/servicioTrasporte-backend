package com.tpi.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Salud sin login
                        .pathMatchers("/actuator/health").permitAll()

                        // Ejemplo: todo transporte sólo para OPERADOR y ADMIN
                        .pathMatchers("/api/transporte/**")
                        .hasAnyRole("OPERADOR","ADMIN")

                        // Tracking para TRANSPORTISTA, OPERADOR y ADMIN
                        .pathMatchers("/api/tracking/**")
                        .hasAnyRole("TRANSPORTISTA","OPERADOR","ADMIN")

                        // Operaciones sólo OPERADOR y ADMIN
                        .pathMatchers("/api/operaciones/**")
                        .hasAnyRole("OPERADOR","ADMIN")

                        // Tarifa sólo ADMIN (ejemplo)
                        .pathMatchers("/api/tarifas/**")
                        .hasRole("ADMIN")

                        // Todo lo demás requiere estar logueado
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
