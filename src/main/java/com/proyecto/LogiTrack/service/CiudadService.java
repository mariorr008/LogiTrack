package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.CiudadDtoRequest;
import com.proyecto.LogiTrack.dto.response.CiudadDtoResponse;

import java.util.List;

public interface CiudadService {

    CiudadDtoResponse guardar(CiudadDtoRequest dto);

    CiudadDtoResponse actualizar(CiudadDtoRequest dto, Integer id);

    void eliminar(Integer id);

    List<CiudadDtoResponse> listar();

    CiudadDtoResponse buscarId(Integer id);
}