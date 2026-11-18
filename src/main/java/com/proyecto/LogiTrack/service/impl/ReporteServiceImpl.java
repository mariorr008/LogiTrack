
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.response.ResumenGeneralDtoResponse;
import com.proyecto.LogiTrack.repository.BodegaRepository;
import com.proyecto.LogiTrack.repository.DetalleMovimientoRepository;
import com.proyecto.LogiTrack.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final BodegaRepository bodegaRepository;
    private final DetalleMovimientoRepository detalleRepository;

    @Override
    public ResumenGeneralDtoResponse obtenerResumenGeneral() {

        // Obtener el stock total de cada bodega.
        var stockPorBodega =
                bodegaRepository.obtenerStockPorBodega();

        // Obtener los cinco productos con mayor cantidad movida.
        var productosMasMovidos =
                detalleRepository.obtenerProductosMasMovidos(
                        PageRequest.of(0, 5)
                );

        return new ResumenGeneralDtoResponse(
                stockPorBodega,
                productosMasMovidos
        );
    }
}
