
package com.proyecto.LogiTrack.security;

import com.proyecto.LogiTrack.model.Admin;
import com.proyecto.LogiTrack.model.Encargado;
import com.proyecto.LogiTrack.model.EstadoPersona;
import com.proyecto.LogiTrack.repository.AdminRepository;
import com.proyecto.LogiTrack.repository.EncargadoRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import com.proyecto.LogiTrack.security.JwtService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtService jwtService;
    private final AdminRepository adminRepository;
    private final EncargadoRepository encargadoRepository;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;

        String header = http.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            String correo = jwtService.validateToken(token);

            if (correo != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                Admin admin = adminRepository
                        .findByPersona_Correo(correo)
                        .orElse(null);

                if (admin != null &&
                        admin.getPersona().getEstado() == EstadoPersona.ACTIVO) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    correo,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);

                } else {

                    Encargado encargado = encargadoRepository
                            .findByPersona_Correo(correo)
                            .orElse(null);

                    if (encargado != null &&
                            encargado.getPersona().getEstado() == EstadoPersona.ACTIVO) {

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        correo,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO"))
                                );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }
}
