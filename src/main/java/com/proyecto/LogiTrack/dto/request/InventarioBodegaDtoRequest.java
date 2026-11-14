
package com.proyecto.LogiTrack.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventarioBodegaDtoRequest(

        @NotNull(message = "La bodega es obligatoria")
        @Positive(message = "El ID de la bodega debe ser positivo")
        Integer bodegaId,

        @NotNull(message = "El producto es obligatorio")
        @Positive(message = "El ID del producto debe ser positivo")
        Integer productoId,

        @NotNull(message = "El stock es obligatorio")
        @Positive(message = "El stock inicial debe ser mayor que cero")
        Integer stock

) {}
