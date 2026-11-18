package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.BodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.BodegaDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.BodegaService;
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
@RequestMapping("/bodegas")
@RequiredArgsConstructor
@Tag(name = "Bodegas", description = "Administración y consulta de bodegas.")
@SecurityRequirement(name = "bearerAuth")
public class BodegaController {

    private final BodegaService bodegaService;

    @PostMapping
    @Operation(summary = "Crear bodega", description = "Requiere rol ADMIN.")
    @RequestBody(required = true, description = "Datos de la bodega.",
            content = @Content(schema = @Schema(implementation = BodegaDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Bodega Central\",\"ciudadId\":1,\"direccion\":\"Calle 10 # 5-20\",\"capacidad\":500,\"encargadoId\":2}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bodega creada.",
                    content = @Content(schema = @Schema(implementation = BodegaDtoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Bodega Central\",\"ciudadId\":1,\"direccion\":\"Calle 10 # 5-20\",\"capacidad\":500,\"encargadoId\":2}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BodegaDtoResponse> guardar(
            @Valid @org.springframework.web.bind.annotation.RequestBody BodegaDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bodega", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la bodega.", example = "1")
    @RequestBody(required = true, description = "Datos actualizados de la bodega.",
            content = @Content(schema = @Schema(implementation = BodegaDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Bodega Norte\",\"ciudadId\":1,\"direccion\":\"Carrera 7 # 20-10\",\"capacidad\":700,\"encargadoId\":2}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bodega actualizada.",
                    content = @Content(schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BodegaDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @org.springframework.web.bind.annotation.RequestBody BodegaDtoRequest dto) {

        return ResponseEntity.ok(bodegaService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bodega", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la bodega.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bodega eliminada."),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La eliminación entra en conflicto con datos relacionados.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        bodegaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar bodegas", description = "Requiere rol ADMIN o EMPLEADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de bodegas.",
                    content = @Content(schema = @Schema(implementation = BodegaDtoResponse.class),
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"Bodega Central\",\"ciudadId\":1,\"direccion\":\"Calle 10 # 5-20\",\"capacidad\":500,\"encargadoId\":2}]"))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<BodegaDtoResponse>> listar() {

        return ResponseEntity.ok(bodegaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar bodega", description = "Requiere rol ADMIN o EMPLEADO.")
    @Parameter(name = "id", description = "Identificador de la bodega.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bodega encontrada.",
                    content = @Content(schema = @Schema(implementation = BodegaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "404", description = "Bodega no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BodegaDtoResponse> buscarId(@PathVariable Integer id) {

        return ResponseEntity.ok(bodegaService.buscarId(id));
    }
}
