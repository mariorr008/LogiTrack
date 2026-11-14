package com.proyecto.LogiTrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CiudadDtoRequest(

        @NotBlank(message = "El nombre de la ciudad es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre

) {
}