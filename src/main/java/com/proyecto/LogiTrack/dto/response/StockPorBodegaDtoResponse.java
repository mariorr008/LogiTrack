
package com.proyecto.LogiTrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockPorBodegaDtoResponse {

    private Integer bodegaId;
    private String bodegaNombre;
    private Long stockTotal;
}
