
package com.proyecto.LogiTrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResumenGeneralDtoResponse {

    private List<StockPorBodegaDtoResponse> stockPorBodega;

    private List<ProductoMasMovidoDtoResponse> productosMasMovidos;
}
