package com.tpi.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
@SuppressWarnings("unused")
public class SecurityConfig {

    /**
     * Extrae roles de realm_access.roles del token de Keycloak
     * y los convierte a authorities con prefijo ROLE_*
     * (ROLE_CLIENTE, ROLE_OPERADOR, ROLE_ADMIN, ROLE_TRANSPORTISTA).
     */
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (!(realmAccess instanceof Map<?, ?> map)) {
            return List.of();
        }

        Object rolesObj = map.get("roles");
        if (!(rolesObj instanceof Collection<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(String::toUpperCase)
                .map(r -> "ROLE_" + r) // ROLE_CLIENTE, ROLE_OPERADOR, etc.
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /**
     * Converter reactivo que construye directamente un JwtAuthenticationToken envuelto en Mono.
     * Evitamos usar ReactiveJwtAuthenticationConverter + adapter para tener control total
     * sobre cómo se convierten las authorities.
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> {
            Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
            AbstractAuthenticationToken auth = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, authorities);
            return Mono.just(auth);
        };
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        // Swagger del gateway (si lo tuvieras)
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()

                        // OPERACIONES: solo OPERADOR o ADMIN
                        .pathMatchers("/api/operaciones/**")
                        .hasAnyRole("OPERADOR", "ADMIN")

                        // TRANSPORTE: Operador, Admin y Transportista
                        .pathMatchers("/api/transporte/**")
                        .hasAnyRole("OPERADOR", "ADMIN", "TRANSPORTISTA")

                        // SEGUIMIENTO: Cliente, Operador, Admin y Transportista
                        .pathMatchers("/api/seguimiento/**")
                        .hasAnyRole("CLIENTE", "OPERADOR", "ADMIN", "TRANSPORTISTA")

                        // TARIFAS: solo Operador y Admin
                        .pathMatchers("/api/tarifa/**")
                        .hasAnyRole("OPERADOR", "ADMIN")

                        // Todo lo demás requiere estar autenticado
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }
}
