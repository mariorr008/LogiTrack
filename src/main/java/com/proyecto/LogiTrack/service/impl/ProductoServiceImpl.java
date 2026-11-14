package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.ProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.ProductoDtoResponse;

import com.proyecto.LogiTrack.mapper.ProductoMapper;

import com.proyecto.LogiTrack.model.Producto;
import com.proyecto.LogiTrack.model.Marca;
import com.proyecto.LogiTrack.model.CategoriaProducto;

import com.proyecto.LogiTrack.repository.ProductoRepository;
import com.proyecto.LogiTrack.repository.MarcaRepository;
import com.proyecto.LogiTrack.repository.CategoriaProductoRepository;
import com.proyecto.LogiTrack.repository.InventarioBodegaRepository;
import com.proyecto.LogiTrack.repository.DetalleMovimientoRepository;

import com.proyecto.LogiTrack.service.ProductoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final InventarioBodegaRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public ProductoDtoResponse guardar(ProductoDtoRequest dto) {

        if (productoRepository.existsByNombre(dto.nombre())) {
            throw new RuntimeException("Ya existe un producto con ese nombre");
        }

        Marca marca = marcaRepository.findById(dto.marcaId())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));

        CategoriaProducto categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Producto producto = productoMapper.dtoToEntity(dto);

        producto.setMarca(marca);
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);

        return productoMapper.entityToDto(guardado);
    }

    @Override
    public ProductoDtoResponse actualizar(ProductoDtoRequest dto, Integer id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (productoRepository.existsByNombreAndIdNot(dto.nombre(), id)) {
            throw new RuntimeException("Ya existe otro producto con ese nombre");
        }

        Marca marca = marcaRepository.findById(dto.marcaId())
                .orElseThrow(() -> new RuntimeException("Marca no encontrada"));

        CategoriaProducto categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        productoMapper.updateEntityFromDto(producto, dto);

        producto.setMarca(marca);
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);

        return productoMapper.entityToDto(actualizado);
    }

    @Override
    public void eliminar(Integer id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (inventarioRepository.existsByProductoId(id)) {
            throw new RuntimeException(
                    "No se puede eliminar el producto porque tiene registros de inventario"
            );
        }

        if (detalleMovimientoRepository.existsByProductoId(id)) {
            throw new RuntimeException(
                    "No se puede eliminar el producto porque tiene movimientos registrados"
            );
        }

        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDtoResponse> listar() {

        return productoRepository.findAll()
                .stream()
                .map(productoMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDtoResponse buscarId(Integer id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return productoMapper.entityToDto(producto);
    }
}