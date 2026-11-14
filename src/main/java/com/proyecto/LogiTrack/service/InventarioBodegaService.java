package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.InventarioBodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.InventarioBodegaDtoResponse;

import java.util.List;

public interface InventarioBodegaService {

    InventarioBodegaDtoResponse guardar(InventarioBodegaDtoRequest dto);

    List<InventarioBodegaDtoResponse> listar();

    InventarioBodegaDtoResponse buscarId(Integer id);

    List<InventarioBodegaDtoResponse> listarPorBodega(Integer bodegaId);

    List<InventarioBodegaDtoResponse> listarPorProducto(Integer productoId);
}
