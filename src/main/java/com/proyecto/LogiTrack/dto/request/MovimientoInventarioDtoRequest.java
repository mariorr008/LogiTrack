
package com.proyecto.LogiTrack.dto.request;

import com.proyecto.LogiTrack.model.TipoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioDtoRequest {

    @NotNull
    private TipoMovimiento tipoMovimiento;

    @NotNull
    private Integer usuarioResponsableId;

    private Integer bodegaOrigenId;

    private Integer bodegaDestinoId;

    @NotEmpty
    @Valid
    private List<DetalleMovimientoDtoRequest> detalles;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleMovimientoDtoRequest {

        @NotNull
        private Integer productoId;

        @NotNull
        @Positive
        private Integer cantidad;
    }
}
