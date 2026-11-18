package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.MovimientoInventarioDtoRequest;
import com.proyecto.LogiTrack.dto.response.MovimientoInventarioDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.model.TipoMovimiento;
import com.proyecto.LogiTrack.service.MovimientoInventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Registro y consulta de movimientos de inventario.")
@SecurityRequirement(name = "bearerAuth")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    @PostMapping
    @Operation(summary = "Registrar movimiento", description = "Requiere rol ADMIN o EMPLEADO.")
    @RequestBody(required = true, description = "Tipo, responsable, bodegas y detalles del movimiento.",
            content = @Content(schema = @Schema(implementation = MovimientoInventarioDtoRequest.class),
                    examples = @ExampleObject(value = "{\"tipoMovimiento\":\"ENTRADA\",\"usuarioResponsableId\":2,\"bodegaOrigenId\":null,\"bodegaDestinoId\":1,\"detalles\":[{\"productoId\":3,\"cantidad\":10}]}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento registrado.",
                    content = @Content(schema = @Schema(implementation = MovimientoInventarioDtoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"fecha\":\"2026-11-17T10:30:00\",\"tipoMovimiento\":\"ENTRADA\",\"usuarioResponsableId\":2,\"bodegaOrigenId\":null,\"bodegaDestinoId\":1,\"detalles\":[{\"productoId\":3,\"productoNombre\":\"Teclado\",\"cantidad\":10}]}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o regla de negocio incumplida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "404", description = "Recurso relacionado no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MovimientoInventarioDtoResponse> registrar(
            @Valid @org.springframework.web.bind.annotation.RequestBody MovimientoInventarioDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar movimientos", description = "Requiere rol ADMIN o EMPLEADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de movimientos.",
                    content = @Content(schema = @Schema(implementation = MovimientoInventarioDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<MovimientoInventarioDtoResponse>> listar() {

        return ResponseEntity.ok(movimientoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar movimiento", description = "Requiere rol ADMIN o EMPLEADO.")
    @Parameter(name = "id", description = "Identificador del movimiento.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado.",
                    content = @Content(schema = @Schema(implementation = MovimientoInventarioDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MovimientoInventarioDtoResponse> buscarId(@PathVariable Integer id) {

        return ResponseEntity.ok(movimientoService.buscarId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar movimientos por usuario", description = "Requiere rol ADMIN o EMPLEADO.")
    @Parameter(name = "usuarioId", description = "Identificador del usuario responsable.", example = "2")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimientos del usuario.",
                    content = @Content(schema = @Schema(implementation = MovimientoInventarioDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<MovimientoInventarioDtoResponse>> listarPorUsuario(
            @PathVariable Integer usuarioId) {

        return ResponseEntity.ok(movimientoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar movimientos por tipo",
            description = "Requiere rol ADMIN o EMPLEADO. Tipos: ENTRADA, SALIDA, TRANSFERENCIA.")
    @Parameter(name = "tipo", description = "Tipo de movimiento: ENTRADA, SALIDA o TRANSFERENCIA.",
            example = "ENTRADA", schema = @Schema(implementation = TipoMovimiento.class))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimientos del tipo indicado.",
                    content = @Content(schema = @Schema(implementation = MovimientoInventarioDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tipo de movimiento inválido.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<MovimientoInventarioDtoResponse>> listarPorTipo(
            @PathVariable TipoMovimiento tipo) {

        return ResponseEntity.ok(movimientoService.listarPorTipo(tipo));
    }

    @GetMapping("/fecha")
    @Operation(summary = "Listar movimientos por rango de fechas",
            description = "Requiere rol ADMIN o EMPLEADO. Las fechas deben usar formato ISO 8601.")
    @Parameter(name = "inicio", in = ParameterIn.QUERY, required = true,
            description = "Inicio del rango (ISO 8601), por ejemplo 2026-11-01T00:00:00.",
            example = "2026-11-01T00:00:00")
    @Parameter(name = "fin", in = ParameterIn.QUERY, required = true,
            description = "Fin del rango (ISO 8601), por ejemplo 2026-11-30T23:59:59.",
            example = "2026-11-30T23:59:59")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimientos del rango de fechas.",
                    content = @Content(schema = @Schema(implementation = MovimientoInventarioDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros ausentes o formato de fecha inválido.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<MovimientoInventarioDtoResponse>> listarPorFechas(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {

        return ResponseEntity.ok(movimientoService.listarPorFechas(inicio, fin));
    }
}
