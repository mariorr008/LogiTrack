
package com.proyecto.LogiTrack.security;

import com.proyecto.LogiTrack.model.Admin;
import com.proyecto.LogiTrack.model.Encargado;
import com.proyecto.LogiTrack.model.EstadoPersona;
import com.proyecto.LogiTrack.repository.AdminRepository;
import com.proyecto.LogiTrack.repository.EncargadoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final EncargadoRepository encargadoRepository;

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Admin admin = adminRepository.findByPersona_Correo(correo)
                .orElse(null);

        if (admin != null) {
            return User.withUsername(correo)
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .disabled(admin.getPersona().getEstado() != EstadoPersona.ACTIVO)
                    .build();
        }

        Encargado encargado = encargadoRepository.findByPersona_Correo(correo)
                .orElse(null);

        if (encargado != null) {
            return User.withUsername(correo)
                    .password(encargado.getPassword())
                    .roles("EMPLEADO")
                    .disabled(encargado.getPersona().getEstado() != EstadoPersona.ACTIVO)
                    .build();
        }

        throw new UsernameNotFoundException(
                "No existe un usuario registrado con ese correo"
        );
    }
}
