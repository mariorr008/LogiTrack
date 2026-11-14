
package com.proyecto.LogiTrack.mapper;

import com.proyecto.LogiTrack.dto.request.BodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.BodegaDtoResponse;
import com.proyecto.LogiTrack.model.Bodega;

import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public Bodega dtoToEntity(BodegaDtoRequest dto) {

        Bodega bodega = new Bodega();

        bodega.setNombre(dto.nombre());
        bodega.setDireccion(dto.direccion());
        bodega.setCapacidad(dto.capacidad());

        return bodega;
    }

    public BodegaDtoResponse entityToDto(Bodega bodega) {

        return new BodegaDtoResponse(
                bodega.getId(),
                bodega.getNombre(),
                bodega.getCiudad().getId(),
                bodega.getDireccion(),
                bodega.getCapacidad(),
                bodega.getEncargado() != null
                        ? bodega.getEncargado().getId()
                        : null
        );
    }

    public void updateEntityFromDto(
            Bodega bodega,
            BodegaDtoRequest dto
    ) {

        bodega.setNombre(dto.nombre());
        bodega.setDireccion(dto.direccion());
        bodega.setCapacidad(dto.capacidad());
    }
}
