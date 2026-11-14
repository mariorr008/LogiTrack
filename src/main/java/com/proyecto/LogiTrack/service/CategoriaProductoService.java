package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.CategoriaProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.CategoriaProductoDtoResponse;

import java.util.List;

public interface CategoriaProductoService {

    CategoriaProductoDtoResponse guardar(CategoriaProductoDtoRequest dto);

    CategoriaProductoDtoResponse actualizar(
            CategoriaProductoDtoRequest dto,
            Integer id
    );

    void eliminar(Integer id);

    List<CategoriaProductoDtoResponse> listar();

    CategoriaProductoDtoResponse buscarId(Integer id);
}