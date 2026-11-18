package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.CiudadDtoRequest;
import com.proyecto.LogiTrack.dto.response.CiudadDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.CiudadService;
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
@RequestMapping("/ciudades")
@RequiredArgsConstructor
@Tag(name = "Ciudades", description = "Administración de ciudades.")
@SecurityRequirement(name = "bearerAuth")
public class CiudadController {

    private final CiudadService ciudadService;

    @PostMapping
    @Operation(summary = "Crear ciudad", description = "Requiere rol ADMIN.")
    @RequestBody(required = true, description = "Nombre de la ciudad.",
            content = @Content(schema = @Schema(implementation = CiudadDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Bogotá\"}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ciudad creada.",
                    content = @Content(schema = @Schema(implementation = CiudadDtoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Bogotá\"}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CiudadDtoResponse> guardar(
            @Valid @org.springframework.web.bind.annotation.RequestBody CiudadDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadService.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ciudad", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la ciudad.", example = "1")
    @RequestBody(required = true, description = "Nuevos datos de la ciudad.",
            content = @Content(schema = @Schema(implementation = CiudadDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Medellín\"}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ciudad actualizada.",
                    content = @Content(schema = @Schema(implementation = CiudadDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Ciudad no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CiudadDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @org.springframework.web.bind.annotation.RequestBody CiudadDtoRequest dto) {

        return ResponseEntity.ok(ciudadService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ciudad", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la ciudad.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ciudad eliminada."),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Ciudad no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La eliminación entra en conflicto con datos relacionados.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        ciudadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar ciudades", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de ciudades.",
                    content = @Content(schema = @Schema(implementation = CiudadDtoResponse.class),
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"Bogotá\"}]"))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CiudadDtoResponse>> listar() {

        return ResponseEntity.ok(ciudadService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar ciudad", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la ciudad.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ciudad encontrada.",
                    content = @Content(schema = @Schema(implementation = CiudadDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Ciudad no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CiudadDtoResponse> buscarId(@PathVariable Integer id) {

        return ResponseEntity.ok(ciudadService.buscarId(id));
    }
}
