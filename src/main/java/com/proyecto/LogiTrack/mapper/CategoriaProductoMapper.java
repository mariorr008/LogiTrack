package com.proyecto.LogiTrack.mapper;

import com.proyecto.LogiTrack.dto.request.CategoriaProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.CategoriaProductoDtoResponse;
import com.proyecto.LogiTrack.model.CategoriaProducto;

import org.springframework.stereotype.Component;

@Component
public class CategoriaProductoMapper {

    public CategoriaProducto dtoToEntity(CategoriaProductoDtoRequest dto) {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombre(dto.nombre());
        return categoria;
    }

    public CategoriaProductoDtoResponse entityToDto(CategoriaProducto categoria) {
        return new CategoriaProductoDtoResponse(
                categoria.getId(),
                categoria.getNombre()
        );
    }

    public void updateEntityFromDto(
            CategoriaProducto categoria,
            CategoriaProductoDtoRequest dto
    ) {
        categoria.setNombre(dto.nombre());
    }
}