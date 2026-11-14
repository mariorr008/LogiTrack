package com.proyecto.LogiTrack.mapper;

import com.proyecto.LogiTrack.dto.request.CiudadDtoRequest;
import com.proyecto.LogiTrack.dto.response.CiudadDtoResponse;
import com.proyecto.LogiTrack.model.Ciudad;
import org.springframework.stereotype.Component;

@Component
public class CiudadMapper {

    public Ciudad dtoToEntity(CiudadDtoRequest dto) {
        if (dto == null) return null;

        Ciudad ciudad = new Ciudad();
        ciudad.setNombre(dto.nombre());

        return ciudad;
    }

    public CiudadDtoResponse entityToDto(Ciudad ciudad) {
        if (ciudad == null) return null;

        return new CiudadDtoResponse(
                ciudad.getId(),
                ciudad.getNombre()
        );
    }

    public void updateEntityFromDto(Ciudad ciudad, CiudadDtoRequest dto) {
        if (ciudad == null || dto == null) return;

        ciudad.setNombre(dto.nombre());
    }
}