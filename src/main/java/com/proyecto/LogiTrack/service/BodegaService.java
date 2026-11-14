package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.BodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.BodegaDtoResponse;

import java.util.List;

public interface BodegaService {

    BodegaDtoResponse guardar(BodegaDtoRequest dto);

    BodegaDtoResponse actualizar(BodegaDtoRequest dto, Integer id);

    void eliminar(Integer id);

    List<BodegaDtoResponse> listar();

    BodegaDtoResponse buscarId(Integer id);
}
