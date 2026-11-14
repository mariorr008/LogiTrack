
package com.proyecto.LogiTrack.mapper;

import com.proyecto.LogiTrack.dto.response.InventarioBodegaDtoResponse;
import com.proyecto.LogiTrack.model.InventarioBodega;

import org.springframework.stereotype.Component;

@Component
public class InventarioBodegaMapper {

    public InventarioBodegaDtoResponse entityToDto(InventarioBodega inventario) {

        return new InventarioBodegaDtoResponse(
                inventario.getId(),
                inventario.getBodega().getId(),
                inventario.getBodega().getNombre(),
                inventario.getProducto().getId(),
                inventario.getProducto().getNombre(),
                inventario.getStock(),
                inventario.getEstado().name()
        );
    }
}
