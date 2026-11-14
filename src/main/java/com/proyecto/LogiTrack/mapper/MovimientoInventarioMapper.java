
package com.proyecto.LogiTrack.mapper;

import com.proyecto.LogiTrack.dto.response.MovimientoInventarioDtoResponse;
import com.proyecto.LogiTrack.model.DetalleMovimiento;
import com.proyecto.LogiTrack.model.MovimientoInventario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimientoInventarioMapper {

    public MovimientoInventarioDtoResponse entityToDto(
            MovimientoInventario movimiento,
            List<DetalleMovimiento> detalles) {

        List<MovimientoInventarioDtoResponse.DetalleMovimientoDtoResponse> detallesDto =
                detalles.stream()
                        .map(detalle ->
                                new MovimientoInventarioDtoResponse.DetalleMovimientoDtoResponse(
                                        detalle.getProducto().getId(),
                                        detalle.getProducto().getNombre(),
                                        detalle.getCantidad()
                                ))
                        .toList();

        return new MovimientoInventarioDtoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                movimiento.getUsuarioResponsable().getId(),
                movimiento.getBodegaOrigen() != null
                        ? movimiento.getBodegaOrigen().getId() : null,
                movimiento.getBodegaDestino() != null
                        ? movimiento.getBodegaDestino().getId() : null,
                detallesDto
        );
    }
}
