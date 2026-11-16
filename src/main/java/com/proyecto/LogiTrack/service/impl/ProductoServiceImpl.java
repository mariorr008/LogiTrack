
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.ProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.ProductoDtoResponse;
import com.proyecto.LogiTrack.exception.BusinessRuleException;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
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
            throw new BusinessRuleException(
                    "Ya existe un producto con ese nombre"
            );
        }

        Marca marca = marcaRepository.findById(dto.marcaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la marca con ID: " + dto.marcaId()
                ));

        CategoriaProducto categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con ID: " + dto.categoriaId()
                ));

        Producto producto = productoMapper.dtoToEntity(dto);

        producto.setMarca(marca);
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);

        return productoMapper.entityToDto(guardado);
    }

    @Override
    public ProductoDtoResponse actualizar(ProductoDtoRequest dto, Integer id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto con ID: " + id
                ));

        if (productoRepository.existsByNombreAndIdNot(dto.nombre(), id)) {
            throw new BusinessRuleException(
                    "Ya existe otro producto con ese nombre"
            );
        }

        Marca marca = marcaRepository.findById(dto.marcaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la marca con ID: " + dto.marcaId()
                ));

        CategoriaProducto categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con ID: " + dto.categoriaId()
                ));

        productoMapper.updateEntityFromDto(producto, dto);

        producto.setMarca(marca);
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);

        return productoMapper.entityToDto(actualizado);
    }

    @Override
    public void eliminar(Integer id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto con ID: " + id
                ));

        if (inventarioRepository.existsByProductoId(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar el producto porque tiene registros de inventario"
            );
        }

        if (detalleMovimientoRepository.existsByProductoId(id)) {
            throw new BusinessRuleException(
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto con ID: " + id
                ));

        return productoMapper.entityToDto(producto);
    }
}
