
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.MarcaDtoRequest;
import com.proyecto.LogiTrack.dto.response.MarcaDtoResponse;
import com.proyecto.LogiTrack.exception.BusinessRuleException;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
import com.proyecto.LogiTrack.mapper.MarcaMapper;
import com.proyecto.LogiTrack.model.Marca;
import com.proyecto.LogiTrack.repository.MarcaRepository;
import com.proyecto.LogiTrack.repository.ProductoRepository;
import com.proyecto.LogiTrack.service.MarcaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;
    private final MarcaMapper marcaMapper;

    @Override
    public MarcaDtoResponse guardar(MarcaDtoRequest dto) {

        if (marcaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessRuleException(
                    "Ya existe una marca con ese nombre"
            );
        }

        Marca marca = marcaMapper.dtoToEntity(dto);

        Marca marcaGuardada = marcaRepository.save(marca);

        return marcaMapper.entityToDto(marcaGuardada);
    }

    @Override
    public MarcaDtoResponse actualizar(MarcaDtoRequest dto, Integer id) {

        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la marca con ID: " + id
                ));

        if (marcaRepository.existsByNombreAndIdNot(dto.nombre(), id)) {
            throw new BusinessRuleException(
                    "Ya existe otra marca con ese nombre"
            );
        }

        marcaMapper.updateEntityFromDto(marca, dto);

        Marca marcaActualizada = marcaRepository.save(marca);

        return marcaMapper.entityToDto(marcaActualizada);
    }

    @Override
    public void eliminar(Integer id) {

        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la marca con ID: " + id
                ));

        if (productoRepository.existsByMarcaId(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la marca porque tiene productos asociados"
            );
        }

        marcaRepository.delete(marca);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarcaDtoResponse> listar() {

        return marcaRepository.findAll()
                .stream()
                .map(marcaMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MarcaDtoResponse buscarId(Integer id) {

        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la marca con ID: " + id
                ));

        return marcaMapper.entityToDto(marca);
    }
}
