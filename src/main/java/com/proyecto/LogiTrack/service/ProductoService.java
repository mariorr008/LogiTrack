package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.ProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.ProductoDtoResponse;

import java.util.List;

public interface ProductoService {

    ProductoDtoResponse guardar(ProductoDtoRequest dto);

    ProductoDtoResponse actualizar(ProductoDtoRequest dto, Integer id);

    void eliminar(Integer id);

    List<ProductoDtoResponse> listar();

    ProductoDtoResponse buscarId(Integer id);
}