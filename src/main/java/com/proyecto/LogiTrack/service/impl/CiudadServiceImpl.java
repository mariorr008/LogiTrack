
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.CiudadDtoRequest;
import com.proyecto.LogiTrack.dto.response.CiudadDtoResponse;
import com.proyecto.LogiTrack.exception.BusinessRuleException;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
import com.proyecto.LogiTrack.mapper.CiudadMapper;
import com.proyecto.LogiTrack.model.Ciudad;
import com.proyecto.LogiTrack.repository.BodegaRepository;
import com.proyecto.LogiTrack.repository.CiudadRepository;
import com.proyecto.LogiTrack.service.CiudadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CiudadServiceImpl implements CiudadService {

    private final CiudadRepository ciudadRepository;
    private final BodegaRepository bodegaRepository;
    private final CiudadMapper ciudadMapper;

    @Override
    public CiudadDtoResponse guardar(CiudadDtoRequest dto) {

        if (ciudadRepository.existsByNombre(dto.nombre())) {
            throw new BusinessRuleException(
                    "La ciudad ya está registrada"
            );
        }

        Ciudad ciudad = ciudadMapper.dtoToEntity(dto);

        return ciudadMapper.entityToDto(ciudadRepository.save(ciudad));
    }

    @Override
    public CiudadDtoResponse actualizar(CiudadDtoRequest dto, Integer id) {

        Ciudad ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la ciudad con ID: " + id
                ));

        if (ciudadRepository.existsByNombreAndIdNot(dto.nombre(), id)) {
            throw new BusinessRuleException(
                    "Ya existe otra ciudad con ese nombre"
            );
        }

        ciudadMapper.updateEntityFromDto(ciudad, dto);

        return ciudadMapper.entityToDto(ciudadRepository.save(ciudad));
    }

    @Override
    public void eliminar(Integer id) {

        Ciudad ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la ciudad con ID: " + id
                ));

        if (bodegaRepository.existsByCiudadId(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar una ciudad que tiene bodegas asociadas"
            );
        }

        ciudadRepository.delete(ciudad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CiudadDtoResponse> listar() {

        return ciudadRepository.findAll()
                .stream()
                .map(ciudadMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CiudadDtoResponse buscarId(Integer id) {

        Ciudad ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la ciudad con ID: " + id
                ));

        return ciudadMapper.entityToDto(ciudad);
    }
}
