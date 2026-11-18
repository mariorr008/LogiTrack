
package com.proyecto.LogiTrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoMasMovidoDtoResponse {

    private Integer productoId;
    private String productoNombre;
    private Long cantidadMovida;
}
