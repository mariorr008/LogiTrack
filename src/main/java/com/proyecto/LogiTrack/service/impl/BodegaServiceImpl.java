
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.BodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.BodegaDtoResponse;
import com.proyecto.LogiTrack.exception.BusinessRuleException;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
import com.proyecto.LogiTrack.mapper.BodegaMapper;
import com.proyecto.LogiTrack.model.Bodega;
import com.proyecto.LogiTrack.model.Ciudad;
import com.proyecto.LogiTrack.model.Encargado;
import com.proyecto.LogiTrack.repository.BodegaRepository;
import com.proyecto.LogiTrack.repository.CiudadRepository;
import com.proyecto.LogiTrack.repository.EncargadoRepository;
import com.proyecto.LogiTrack.repository.InventarioBodegaRepository;
import com.proyecto.LogiTrack.repository.MovimientoInventarioRepository;
import com.proyecto.LogiTrack.service.BodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BodegaServiceImpl implements BodegaService {

    private final BodegaRepository bodegaRepository;
    private final CiudadRepository ciudadRepository;
    private final EncargadoRepository encargadoRepository;
    private final InventarioBodegaRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final BodegaMapper bodegaMapper;

    @Override
    public BodegaDtoResponse guardar(BodegaDtoRequest dto) {

        if (bodegaRepository.existsByNombre(dto.nombre())) {
            throw new BusinessRuleException(
                    "Ya existe una bodega con ese nombre"
            );
        }

        Ciudad ciudad = ciudadRepository.findById(dto.ciudadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la ciudad con ID: " + dto.ciudadId()
                ));

        Bodega bodega = bodegaMapper.dtoToEntity(dto);
        bodega.setCiudad(ciudad);

        if (dto.encargadoId() != null) {

            Encargado encargado = encargadoRepository.findById(dto.encargadoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró el encargado con ID: " + dto.encargadoId()
                    ));

            if (bodegaRepository.existsByEncargadoId(dto.encargadoId())) {
                throw new BusinessRuleException(
                        "El encargado ya tiene una bodega asignada"
                );
            }

            bodega.setEncargado(encargado);
        }

        return bodegaMapper.entityToDto(bodegaRepository.save(bodega));
    }

    @Override
    public BodegaDtoResponse actualizar(BodegaDtoRequest dto, Integer id) {

        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la bodega con ID: " + id
                ));

        if (bodegaRepository.existsByNombreAndIdNot(dto.nombre(), id)) {
            throw new BusinessRuleException(
                    "Ya existe otra bodega con ese nombre"
            );
        }

        Ciudad ciudad = ciudadRepository.findById(dto.ciudadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la ciudad con ID: " + dto.ciudadId()
                ));

        Long stockActual = inventarioRepository.sumarStockPorBodega(id);

        if (dto.capacidad() < stockActual) {
            throw new BusinessRuleException(
                    "La capacidad no puede ser menor que el inventario actual"
            );
        }

        bodegaMapper.updateEntityFromDto(bodega, dto);
        bodega.setCiudad(ciudad);

        if (dto.encargadoId() == null) {

            bodega.setEncargado(null);

        } else {

            Encargado encargado = encargadoRepository.findById(dto.encargadoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No se encontró el encargado con ID: " + dto.encargadoId()
                    ));

            if (bodegaRepository.existsByEncargadoIdAndIdNot(
                    dto.encargadoId(), id)) {
                throw new BusinessRuleException(
                        "El encargado ya tiene otra bodega asignada"
                );
            }

            bodega.setEncargado(encargado);
        }

        return bodegaMapper.entityToDto(bodegaRepository.save(bodega));
    }

    @Override
    public void eliminar(Integer id) {

        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la bodega con ID: " + id
                ));

        if (inventarioRepository.existsByBodegaId(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la bodega porque tiene registros de inventario"
            );
        }

        if (movimientoRepository.existsByBodegaOrigenId(id)
                || movimientoRepository.existsByBodegaDestinoId(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la bodega porque tiene movimientos registrados"
            );
        }

        bodegaRepository.delete(bodega);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodegaDtoResponse> listar() {

        return bodegaRepository.findAll()
                .stream()
                .map(bodegaMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BodegaDtoResponse buscarId(Integer id) {

        Bodega bodega = bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró la bodega con ID: " + id
                ));

        return bodegaMapper.entityToDto(bodega);
    }
}
