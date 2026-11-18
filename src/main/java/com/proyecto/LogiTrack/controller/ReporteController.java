
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.response.ResumenGeneralDtoResponse;
import com.proyecto.LogiTrack.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    public ResponseEntity<ResumenGeneralDtoResponse> obtenerResumenGeneral() {

        return ResponseEntity.ok(
                reporteService.obtenerResumenGeneral()
        );
    }
}
