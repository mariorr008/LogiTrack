
package com.proyecto.LogiTrack.config;

import com.proyecto.LogiTrack.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Documentación OpenAPI/Swagger; las rutas de negocio conservan sus permisos.
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Endpoints públicos
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // Solicitudes CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // BODEGAS: ambos consultan, solo ADMIN administra
                        .requestMatchers(HttpMethod.GET, "/bodegas/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")

                        .requestMatchers("/bodegas/**")
                        .hasRole("ADMIN")

                        // PRODUCTOS: ambos consultan, solo ADMIN administra
                        .requestMatchers(HttpMethod.GET, "/productos/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")

                        .requestMatchers("/productos/**")
                        .hasRole("ADMIN")

                        // INVENTARIOS: ambos consultan
                        .requestMatchers(HttpMethod.GET, "/inventarios/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")

                        // Solo ADMIN puede crear registros de inventario
                        .requestMatchers("/inventarios/**")
                        .hasRole("ADMIN")

                        // MOVIMIENTOS: ambos consultan y registran
                        .requestMatchers("/movimientos/**")
                        .hasAnyRole("ADMIN", "EMPLEADO")

                        // CIUDADES: solo ADMIN administra
                        .requestMatchers("/ciudades/**")
                        .hasRole("ADMIN")

                        // MARCAS: solo ADMIN administra
                        .requestMatchers("/marcas/**")
                        .hasRole("ADMIN")

                        // CATEGORÍAS: solo ADMIN administra
                        .requestMatchers("/categorias/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/reportes/**")
                        .hasRole("ADMIN")

                        // AUDITORÍAS: solo ADMIN puede consultar los registros
                        .requestMatchers("/auditorias/**")
                        .hasRole("ADMIN")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
