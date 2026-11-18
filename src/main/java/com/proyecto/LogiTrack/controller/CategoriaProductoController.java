package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.CategoriaProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.CategoriaProductoDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.CategoriaProductoService;
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
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Administración de categorías de productos.")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaService;

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Requiere rol ADMIN.")
    @RequestBody(required = true, description = "Nombre de la categoría.",
            content = @Content(schema = @Schema(implementation = CategoriaProductoDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Electrónica\"}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada.",
                    content = @Content(schema = @Schema(implementation = CategoriaProductoDtoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Electrónica\"}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoriaProductoDtoResponse> guardar(
            @Valid @org.springframework.web.bind.annotation.RequestBody CategoriaProductoDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la categoría.", example = "1")
    @RequestBody(required = true, description = "Nuevo nombre de la categoría.",
            content = @Content(schema = @Schema(implementation = CategoriaProductoDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Accesorios\"}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada.",
                    content = @Content(schema = @Schema(implementation = CategoriaProductoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoriaProductoDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @org.springframework.web.bind.annotation.RequestBody CategoriaProductoDtoRequest dto) {

        return ResponseEntity.ok(categoriaService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la categoría.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada."),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La eliminación entra en conflicto con datos relacionados.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorías.",
                    content = @Content(schema = @Schema(implementation = CategoriaProductoDtoResponse.class),
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"Electrónica\"}]"))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CategoriaProductoDtoResponse>> listar() {

        return ResponseEntity.ok(categoriaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar categoría", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador de la categoría.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada.",
                    content = @Content(schema = @Schema(implementation = CategoriaProductoDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoriaProductoDtoResponse> buscarId(@PathVariable Integer id) {

        return ResponseEntity.ok(categoriaService.buscarId(id));
    }
}
