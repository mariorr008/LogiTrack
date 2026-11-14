package com.proyecto.LogiTrack.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoDtoRequest(

        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotNull(message = "La marca es obligatoria")
        @Positive(message = "El ID de la marca debe ser positivo")
        Integer marcaId,

        @NotNull(message = "La categoría es obligatoria")
        @Positive(message = "El ID de la categoría debe ser positivo")
        Integer categoriaId,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
        BigDecimal precio

) {}