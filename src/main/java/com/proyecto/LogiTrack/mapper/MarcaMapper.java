package com.proyecto.LogiTrack.mapper;

import com.proyecto.LogiTrack.dto.request.MarcaDtoRequest;
import com.proyecto.LogiTrack.dto.response.MarcaDtoResponse;
import com.proyecto.LogiTrack.model.Marca;
import org.springframework.stereotype.Component;

@Component
public class MarcaMapper {

    public Marca dtoToEntity(MarcaDtoRequest dto) {
        Marca marca = new Marca();
        marca.setNombre(dto.nombre());
        return marca;
    }

    public MarcaDtoResponse entityToDto(Marca marca) {
        return new MarcaDtoResponse(
                marca.getId(),
                marca.getNombre()
        );
    }

    public void updateEntityFromDto(Marca marca, MarcaDtoRequest dto) {
        marca.setNombre(dto.nombre());
    }
}