
package com.proyecto.LogiTrack.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.LogiTrack.dto.response.AuditoriaDtoResponse;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
import com.proyecto.LogiTrack.model.Auditoria;
import com.proyecto.LogiTrack.model.Persona;
import com.proyecto.LogiTrack.model.TipoOperacion;
import com.proyecto.LogiTrack.repository.AuditoriaRepository;
import com.proyecto.LogiTrack.repository.PersonaRepository;
import com.proyecto.LogiTrack.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final PersonaRepository personaRepository;

    // Objeto encargado de convertir los datos a JSON.
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void registrar(
            TipoOperacion tipoOperacion,
            String entidadAfectada,
            Integer registroAfectadoId,
            Object valoresAnteriores,
            Object valoresNuevos) {

        Authentication autenticacion = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()
                || autenticacion.getPrincipal().equals("anonymousUser")) {

            throw new IllegalStateException(
                    "No se encontró un usuario autenticado para registrar la auditoría"
            );
        }

        String correo = autenticacion.getName();

        Persona usuario = personaRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el usuario responsable de la operación"
                        ));

        Auditoria auditoria = new Auditoria();

        auditoria.setFechaHora(LocalDateTime.now());
        auditoria.setTipoOperacion(tipoOperacion);
        auditoria.setUsuario(usuario);
        auditoria.setEntidadAfectada(entidadAfectada);
        auditoria.setRegistroAfectadoId(registroAfectadoId);
        auditoria.setValoresAnteriores(convertirJson(valoresAnteriores));
        auditoria.setValoresNuevos(convertirJson(valoresNuevos));

        auditoriaRepository.save(auditoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDtoResponse> listar() {

        return auditoriaRepository.findAll()
                .stream()
                .map(this::convertirDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaDtoResponse buscarPorId(Integer id) {

        Auditoria auditoria = auditoriaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la auditoría con ID: " + id
                        ));

        return convertirDto(auditoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDtoResponse> listarPorUsuario(Integer usuarioId) {

        if (!personaRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException(
                    "No se encontró la persona con ID: " + usuarioId
            );
        }

        return auditoriaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::convertirDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDtoResponse> listarPorTipo(
            TipoOperacion tipoOperacion) {

        if (tipoOperacion == null) {
            throw new IllegalArgumentException(
                    "El tipo de operación es obligatorio"
            );
        }

        return auditoriaRepository.findByTipoOperacion(tipoOperacion)
                .stream()
                .map(this::convertirDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaDtoResponse> listarPorEntidad(
            String entidadAfectada) {

        if (entidadAfectada == null || entidadAfectada.isBlank()) {
            throw new IllegalArgumentException(
                    "La entidad afectada es obligatoria"
            );
        }

        return auditoriaRepository.findByEntidadAfectada(
                        entidadAfectada.toUpperCase()
                )
                .stream()
                .map(this::convertirDto)
                .toList();
    }

    private AuditoriaDtoResponse convertirDto(Auditoria auditoria) {

        return new AuditoriaDtoResponse(
                auditoria.getId(),
                auditoria.getFechaHora(),
                auditoria.getTipoOperacion(),
                auditoria.getUsuario().getId(),
                auditoria.getEntidadAfectada(),
                auditoria.getRegistroAfectadoId(),
                auditoria.getValoresAnteriores(),
                auditoria.getValoresNuevos()
        );
    }

    private String convertirJson(Object valores) {

        if (valores == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(valores);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "No fue posible convertir los datos de auditoría a JSON",
                    e
            );
        }
    }
}
