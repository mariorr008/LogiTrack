
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.CategoriaProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.CategoriaProductoDtoResponse;
import com.proyecto.LogiTrack.exception.BusinessRuleException;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
import com.proyecto.LogiTrack.mapper.CategoriaProductoMapper;
import com.proyecto.LogiTrack.model.CategoriaProducto;
import com.proyecto.LogiTrack.repository.CategoriaProductoRepository;
import com.proyecto.LogiTrack.repository.ProductoRepository;
import com.proyecto.LogiTrack.service.CategoriaProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaProductoServiceImpl implements CategoriaProductoService {

    private final CategoriaProductoRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaProductoMapper categoriaMapper;

    @Override
    public CategoriaProductoDtoResponse guardar(CategoriaProductoDtoRequest dto) {

        if (categoriaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessRuleException(
                    "Ya existe una categoría con ese nombre"
            );
        }

        CategoriaProducto categoria = categoriaMapper.dtoToEntity(dto);

        CategoriaProducto guardada = categoriaRepository.save(categoria);

        return categoriaMapper.entityToDto(guardada);
    }

    @Override
    public CategoriaProductoDtoResponse actualizar(
            CategoriaProductoDtoRequest dto,
            Integer id
    ) {

        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con ID: " + id
                ));

        if (categoriaRepository.existsByNombreAndIdNot(dto.nombre(), id)) {
            throw new BusinessRuleException(
                    "Ya existe otra categoría con ese nombre"
            );
        }

        categoriaMapper.updateEntityFromDto(categoria, dto);

        CategoriaProducto actualizada = categoriaRepository.save(categoria);

        return categoriaMapper.entityToDto(actualizada);
    }

    @Override
    public void eliminar(Integer id) {

        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con ID: " + id
                ));

        if (productoRepository.existsByCategoriaId(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la categoría porque tiene productos asociados"
            );
        }

        categoriaRepository.delete(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProductoDtoResponse> listar() {

        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProductoDtoResponse buscarId(Integer id) {

        CategoriaProducto categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la categoría con ID: " + id
                ));

        return categoriaMapper.entityToDto(categoria);
    }
}
