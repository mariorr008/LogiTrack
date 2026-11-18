
package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.response.AuditoriaDtoResponse;
import com.proyecto.LogiTrack.model.TipoOperacion;

import java.util.List;

public interface AuditoriaService {

    void registrar(
            TipoOperacion tipoOperacion,
            String entidadAfectada,
            Integer registroAfectadoId,
            Object valoresAnteriores,
            Object valoresNuevos
    );

    List<AuditoriaDtoResponse> listar();

    AuditoriaDtoResponse buscarPorId(Integer id);

    List<AuditoriaDtoResponse> listarPorUsuario(Integer usuarioId);

    List<AuditoriaDtoResponse> listarPorTipo(TipoOperacion tipoOperacion);

    List<AuditoriaDtoResponse> listarPorEntidad(String entidadAfectada);
}
