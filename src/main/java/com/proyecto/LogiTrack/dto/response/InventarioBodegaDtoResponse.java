
package com.proyecto.LogiTrack.dto.response;

public record InventarioBodegaDtoResponse(
        Integer id,
        Integer bodegaId,
        String bodegaNombre,
        Integer productoId,
        String productoNombre,
        Integer stock,
        String estado
) {}
