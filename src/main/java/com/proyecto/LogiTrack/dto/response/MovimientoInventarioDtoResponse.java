
package com.proyecto.LogiTrack.dto.response;

import com.proyecto.LogiTrack.model.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioDtoResponse {

    private Integer id;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private Integer usuarioResponsableId;
    private Integer bodegaOrigenId;
    private Integer bodegaDestinoId;
    private List<DetalleMovimientoDtoResponse> detalles;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleMovimientoDtoResponse {

        private Integer productoId;
        private String productoNombre;
        private Integer cantidad;
    }
}
