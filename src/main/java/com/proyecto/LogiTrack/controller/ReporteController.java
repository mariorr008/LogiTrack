package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.response.ResumenGeneralDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Consultas agregadas para el seguimiento del inventario.")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    @Operation(summary = "Obtener resumen general", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen de stock y productos más movidos.",
                    content = @Content(schema = @Schema(implementation = ResumenGeneralDtoResponse.class),
                            examples = @ExampleObject(value = "{\"stockPorBodega\":[{\"bodegaId\":1,\"bodegaNombre\":\"Bodega Central\",\"stockTotal\":250}],\"productosMasMovidos\":[{\"productoId\":3,\"productoNombre\":\"Teclado\",\"cantidadMovida\":80}]}"))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResumenGeneralDtoResponse> obtenerResumenGeneral() {

        return ResponseEntity.ok(reporteService.obtenerResumenGeneral());
    }
}
