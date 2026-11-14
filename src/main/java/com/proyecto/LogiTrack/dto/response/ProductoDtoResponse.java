package com.proyecto.LogiTrack.dto.response;

import java.math.BigDecimal;

public record ProductoDtoResponse(
        Integer id,
        String nombre,
        Integer marcaId,
        String marcaNombre,
        Integer categoriaId,
        String categoriaNombre,
        BigDecimal precio
) {}