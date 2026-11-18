
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.ProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.ProductoDtoResponse;
import com.proyecto.LogiTrack.exception.BusinessRuleException;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
import com.proyecto.LogiTrack.mapper.ProductoMapper;
import com.proyecto.LogiTrack.model.*;
import com.proyecto.LogiTrack.repository.*;
import com.proyecto.LogiTrack.service.AuditoriaService;
import com.proyecto.LogiTrack.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final AuditoriaService auditoriaService;

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

        auditoriaService.registrar(
                TipoOperacion.INSERT,
                "PRODUCTO",
                guardado.getId(),
                null,
                obtenerDatosProducto(guardado)
        );

        return productoMapper.entityToDto(guardado);
    }

    @Override
    public ProductoDtoResponse actualizar(ProductoDtoRequest dto, Integer id) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el producto con ID: " + id
                ));

        Map<String, Object> valoresAnteriores =
                obtenerDatosProducto(producto);

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

        auditoriaService.registrar(
                TipoOperacion.UPDATE,
                "PRODUCTO",
                actualizado.getId(),
                valoresAnteriores,
                obtenerDatosProducto(actualizado)
        );

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

        Map<String, Object> valoresAnteriores =
                obtenerDatosProducto(producto);

        productoRepository.delete(producto);

        auditoriaService.registrar(
                TipoOperacion.DELETE,
                "PRODUCTO",
                id,
                valoresAnteriores,
                null
        );
    }

    private Map<String, Object> obtenerDatosProducto(Producto producto) {

        Map<String, Object> datos = new LinkedHashMap<>();

        datos.put("id", producto.getId());
        datos.put("nombre", producto.getNombre());
        datos.put("precio", producto.getPrecio());
        datos.put("marcaId", producto.getMarca().getId());
        datos.put("categoriaId", producto.getCategoria().getId());

        return datos;
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
