package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.InventarioBodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.InventarioBodegaDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.InventarioBodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventarios")
@RequiredArgsConstructor
@Tag(name = "Inventarios", description = "Consulta de inventario por bodega y producto.")
@SecurityRequirement(name = "bearerAuth")
public class InventarioBodegaController {

    private final InventarioBodegaService inventarioService;

    @PostMapping
    @Operation(summary = "Crear registro de inventario", description = "Requiere rol ADMIN.")
    @RequestBody(required = true, description = "Bodega, producto y stock inicial.",
            content = @Content(schema = @Schema(implementation = InventarioBodegaDtoRequest.class),
                    examples = @ExampleObject(value = "{\"bodegaId\":1,\"productoId\":3,\"stock\":25}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro de inventario creado.",
                    content = @Content(schema = @Schema(implementation = InventarioBodegaDtoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"bodegaId\":1,\"bodegaNombre\":\"Bodega Central\",\"productoId\":3,\"productoNombre\":\"Teclado\",\"stock\":25,\"estado\":\"DISPONIBLE\"}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Bodega o producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<InventarioBodegaDtoResponse> guardar(
            @Valid @org.springframework.web.bind.annotation.RequestBody InventarioBodegaDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.guardar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar inventario", description = "Requiere rol ADMIN o EMPLEADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de registros de inventario.",
                    content = @Content(schema = @Schema(implementation = InventarioBodegaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listar() {

        return ResponseEntity.ok(inventarioService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar registro de inventario", description = "Requiere rol ADMIN o EMPLEADO.")
    @Parameter(name = "id", description = "Identificador del registro de inventario.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado.",
                    content = @Content(schema = @Schema(implementation = InventarioBodegaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<InventarioBodegaDtoResponse> buscarId(@PathVariable Integer id) {

        return ResponseEntity.ok(inventarioService.buscarId(id));
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Listar inventario por bodega", description = "Requiere rol ADMIN o EMPLEADO.")
    @Parameter(name = "bodegaId", description = "Identificador de la bodega.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario de la bodega.",
                    content = @Content(schema = @Schema(implementation = InventarioBodegaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listarPorBodega(
            @PathVariable Integer bodegaId) {

        return ResponseEntity.ok(inventarioService.listarPorBodega(bodegaId));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Listar inventario por producto", description = "Requiere rol ADMIN o EMPLEADO.")
    @Parameter(name = "productoId", description = "Identificador del producto.", example = "3")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario del producto.",
                    content = @Content(schema = @Schema(implementation = InventarioBodegaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listarPorProducto(
            @PathVariable Integer productoId) {

        return ResponseEntity.ok(inventarioService.listarPorProducto(productoId));
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Listar inventario con stock bajo",
            description = "Consulta productos con menos de 10 unidades. Requiere rol ADMIN o EMPLEADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registros con stock bajo.",
                    content = @Content(schema = @Schema(implementation = InventarioBodegaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listarStockBajo() {

        return ResponseEntity.ok(inventarioService.listarStockBajo());
    }
}
