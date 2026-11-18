
package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.MovimientoInventarioDtoRequest;
import com.proyecto.LogiTrack.dto.response.MovimientoInventarioDtoResponse;
import com.proyecto.LogiTrack.model.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioService {

    MovimientoInventarioDtoResponse registrar(MovimientoInventarioDtoRequest dto);

    List<MovimientoInventarioDtoResponse> listar();

    MovimientoInventarioDtoResponse buscarId(Integer id);

    List<MovimientoInventarioDtoResponse> listarPorUsuario(Integer usuarioId);

    List<MovimientoInventarioDtoResponse> listarPorTipo(TipoMovimiento tipo);

    List<MovimientoInventarioDtoResponse> listarPorFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}
