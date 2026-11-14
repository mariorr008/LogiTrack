package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.InventarioBodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.InventarioBodegaDtoResponse;

import com.proyecto.LogiTrack.mapper.InventarioBodegaMapper;

import com.proyecto.LogiTrack.model.Bodega;
import com.proyecto.LogiTrack.model.Producto;
import com.proyecto.LogiTrack.model.InventarioBodega;
import com.proyecto.LogiTrack.model.EstadoInventario;

import com.proyecto.LogiTrack.repository.BodegaRepository;
import com.proyecto.LogiTrack.repository.ProductoRepository;
import com.proyecto.LogiTrack.repository.InventarioBodegaRepository;

import com.proyecto.LogiTrack.service.InventarioBodegaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioBodegaServiceImpl implements InventarioBodegaService {

    private final InventarioBodegaRepository inventarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioBodegaMapper inventarioMapper;

    @Override
    public InventarioBodegaDtoResponse guardar(InventarioBodegaDtoRequest dto) {

        Bodega bodega = bodegaRepository.findById(dto.bodegaId())
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));

        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (inventarioRepository.findByBodegaIdAndProductoId(
                dto.bodegaId(), dto.productoId()).isPresent()) {
            throw new RuntimeException(
                    "El producto ya tiene un registro de inventario en esta bodega"
            );
        }

        Long stockActual = inventarioRepository.sumarStockPorBodega(dto.bodegaId());

        if (stockActual + dto.stock() > bodega.getCapacidad()) {
            throw new RuntimeException(
                    "La cantidad supera la capacidad disponible de la bodega"
            );
        }

        InventarioBodega inventario = new InventarioBodega();

        inventario.setBodega(bodega);
        inventario.setProducto(producto);
        inventario.setStock(dto.stock());

        inventario.setEstado(EstadoInventario.ACTIVO);

        InventarioBodega guardado = inventarioRepository.save(inventario);

        return inventarioMapper.entityToDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioBodegaDtoResponse> listar() {

        return inventarioRepository.findAll()
                .stream()
                .map(inventarioMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioBodegaDtoResponse buscarId(Integer id) {

        InventarioBodega inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

        return inventarioMapper.entityToDto(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioBodegaDtoResponse> listarPorBodega(Integer bodegaId) {

        if (!bodegaRepository.existsById(bodegaId)) {
            throw new RuntimeException("Bodega no encontrada");
        }

        return inventarioRepository.findByBodegaId(bodegaId)
                .stream()
                .map(inventarioMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioBodegaDtoResponse> listarPorProducto(Integer productoId) {

        if (!productoRepository.existsById(productoId)) {
            throw new RuntimeException("Producto no encontrado");
        }

        return inventarioRepository.findByProductoId(productoId)
                .stream()
                .map(inventarioMapper::entityToDto)
                .toList();
    }
}
