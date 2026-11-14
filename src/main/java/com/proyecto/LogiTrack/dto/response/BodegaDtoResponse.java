
package com.proyecto.LogiTrack.dto.response;

public record BodegaDtoResponse(
        Integer id,
        String nombre,
        Integer ciudadId,
        String direccion,
        Integer capacidad,
        Integer encargadoId
) {}
