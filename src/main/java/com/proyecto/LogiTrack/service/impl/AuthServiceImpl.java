
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.LoginRequest;
import com.proyecto.LogiTrack.dto.request.RegistroRequest;
import com.proyecto.LogiTrack.dto.response.EmpleadoPendienteDtoResponse;
import com.proyecto.LogiTrack.dto.response.LoginResponse;
import com.proyecto.LogiTrack.model.Admin;
import com.proyecto.LogiTrack.model.Encargado;
import com.proyecto.LogiTrack.model.EstadoPersona;
import com.proyecto.LogiTrack.model.Persona;
import com.proyecto.LogiTrack.repository.AdminRepository;
import com.proyecto.LogiTrack.repository.EncargadoRepository;
import com.proyecto.LogiTrack.repository.PersonaRepository;
import com.proyecto.LogiTrack.security.JwtService;
import com.proyecto.LogiTrack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final EncargadoRepository encargadoRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse iniciarSesion(LoginRequest request) {

        String correo = request.correo().trim();

        Admin admin = adminRepository.findByPersona_Correo(correo).orElse(null);

        if (admin != null) {
            if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
                throw new BadCredentialsException("Correo o contraseña incorrectos");
            }

            if (admin.getPersona().getEstado() != EstadoPersona.ACTIVO) {
                throw new BadCredentialsException("Correo o contraseña incorrectos");
            }

            String token = jwtService.generateToken(correo);
            return new LoginResponse(token);
        }

        Encargado encargado = encargadoRepository.findByPersona_Correo(correo).orElse(null);

        if (encargado != null) {
            if (!passwordEncoder.matches(request.password(), encargado.getPassword())) {
                throw new BadCredentialsException("Correo o contraseña incorrectos");
            }

            if (encargado.getPersona().getEstado() != EstadoPersona.ACTIVO) {
                throw new BadCredentialsException("Correo o contraseña incorrectos");
            }

            String token = jwtService.generateToken(correo);
            return new LoginResponse(token);
        }

        throw new BadCredentialsException("Correo o contraseña incorrectos");
    }

    @Override
    @Transactional
    public void registrarEmpleado(RegistroRequest request) {

        String correo = request.correo().trim();
        String cedula = request.cedula().trim();

        if (personaRepository.existsByCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        if (personaRepository.existsByCedula(cedula)) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        Persona persona = new Persona();

        persona.setNombre(request.nombre().trim());
        persona.setCorreo(correo);
        persona.setTelefono(request.telefono());
        persona.setCedula(cedula);
        persona.setEstado(EstadoPersona.PENDIENTE);

        personaRepository.save(persona);

        Encargado encargado = new Encargado();

        encargado.setPersona(persona);
        encargado.setPassword(passwordEncoder.encode(request.password()));
        encargado.setDebeCambiarPassword(false);

        encargadoRepository.save(encargado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoPendienteDtoResponse> listarEmpleadosPendientes() {

        return encargadoRepository.findAll().stream()
                .filter(e -> e.getPersona().getEstado() == EstadoPersona.PENDIENTE)
                .map(e -> new EmpleadoPendienteDtoResponse(
                        e.getPersona().getId(),
                        e.getPersona().getNombre(),
                        e.getPersona().getCorreo(),
                        e.getPersona().getTelefono(),
                        e.getPersona().getCedula(),
                        e.getPersona().getEstado().name()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void activarEmpleado(Integer id) {

        Encargado encargado = encargadoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Empleado no encontrado")
                );

        Persona persona = encargado.getPersona();

        if (persona.getEstado() != EstadoPersona.PENDIENTE) {
            throw new IllegalArgumentException(
                    "El empleado no tiene una cuenta pendiente"
            );
        }

        persona.setEstado(EstadoPersona.ACTIVO);

        personaRepository.save(persona);
    }
}
