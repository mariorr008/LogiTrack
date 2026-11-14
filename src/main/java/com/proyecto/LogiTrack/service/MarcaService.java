package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.MarcaDtoRequest;
import com.proyecto.LogiTrack.dto.response.MarcaDtoResponse;

import java.util.List;

public interface MarcaService {

    MarcaDtoResponse guardar(MarcaDtoRequest dto);

    MarcaDtoResponse actualizar(MarcaDtoRequest dto, Integer id);

    void eliminar(Integer id);

    List<MarcaDtoResponse> listar();

    MarcaDtoResponse buscarId(Integer id);
}