package com.proyecto.LogiTrack.mapper;

import com.proyecto.LogiTrack.dto.request.ProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.ProductoDtoResponse;
import com.proyecto.LogiTrack.model.Producto;

import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto dtoToEntity(ProductoDtoRequest dto) {

        Producto producto = new Producto();

        producto.setNombre(dto.nombre());
        producto.setPrecio(dto.precio());

        return producto;
    }

    public ProductoDtoResponse entityToDto(Producto producto) {

        return new ProductoDtoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getMarca().getId(),
                producto.getMarca().getNombre(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getPrecio()
        );
    }

    public void updateEntityFromDto(
            Producto producto,
            ProductoDtoRequest dto
    ) {

        producto.setNombre(dto.nombre());
        producto.setPrecio(dto.precio());
    }
}