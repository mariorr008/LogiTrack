package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.MarcaDtoRequest;
import com.proyecto.LogiTrack.dto.response.MarcaDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.MarcaService;
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
@RequestMapping("/marcas")
@RequiredArgsConstructor
@Tag(name = "Marcas", description = "Administración de marcas de productos.")
@SecurityRequirement(name = "bearerAuth")
public class MarcaController {

    private final MarcaService marcaService;

    @PostMapping
    @Operation(summary = "Crear marca", description = "Requiere rol ADMIN.")
    @RequestBody(required = true, description = "Nombre de la marca.",
            content = @Content(schema = @Schema(implementation = MarcaDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Acme\"}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Marca creada.",
                    content = @Content(schema = @Schema(implementation = MarcaDtoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Acme\"}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MarcaDtoResponse> guardar(
            @Valid @org.springframework.web.bind.annotation.RequestBody MarcaDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(marcaService.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar marca", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la marca.", example = "1")
    @RequestBody(required = true, description = "Nuevo nombre de la marca.",
            content = @Content(schema = @Schema(implementation = MarcaDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Acme Plus\"}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marca actualizada.",
                    content = @Content(schema = @Schema(implementation = MarcaDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MarcaDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @org.springframework.web.bind.annotation.RequestBody MarcaDtoRequest dto) {

        return ResponseEntity.ok(marcaService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar marca", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la marca.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Marca eliminada."),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La eliminación entra en conflicto con datos relacionados.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        marcaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar marcas", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de marcas.",
                    content = @Content(schema = @Schema(implementation = MarcaDtoResponse.class),
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"Acme\"}]"))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<MarcaDtoResponse>> listar() {

        return ResponseEntity.ok(marcaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar marca", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la marca.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marca encontrada.",
                    content = @Content(schema = @Schema(implementation = MarcaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Marca no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MarcaDtoResponse> buscarId(@PathVariable Integer id) {

        return ResponseEntity.ok(marcaService.buscarId(id));
    }
}
