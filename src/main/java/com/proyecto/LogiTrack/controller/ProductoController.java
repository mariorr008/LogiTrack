package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.ProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.ProductoDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.ProductoService;
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
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Administración y consulta de productos.")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    @Operation(summary = "Crear producto", description = "Requiere rol ADMIN.")
    @RequestBody(required = true, description = "Datos del producto.",
            content = @Content(schema = @Schema(implementation = ProductoDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Teclado\",\"marcaId\":1,\"categoriaId\":2,\"precio\":49.99}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado.",
                    content = @Content(schema = @Schema(implementation = ProductoDtoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Teclado\",\"marcaId\":1,\"marcaNombre\":\"Acme\",\"categoriaId\":2,\"categoriaNombre\":\"Electrónica\",\"precio\":49.99}"))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDtoResponse> guardar(
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductoDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador del producto.", example = "1")
    @RequestBody(required = true, description = "Datos actualizados del producto.",
            content = @Content(schema = @Schema(implementation = ProductoDtoRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Teclado inalámbrico\",\"marcaId\":1,\"categoriaId\":2,\"precio\":59.99}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado.",
                    content = @Content(schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductoDtoRequest dto) {

        return ResponseEntity.ok(productoService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Requiere rol ADMIN.")
    @Parameter(name = "id", description = "Identificador del producto.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado."),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La eliminación entra en conflicto con datos relacionados.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Requiere rol ADMIN o EMPLEADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos.",
                    content = @Content(schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProductoDtoResponse>> listar() {

        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar producto", description = "Requiere rol ADMIN o EMPLEADO.")
    @Parameter(name = "id", description = "Identificador del producto.", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado.",
                    content = @Content(schema = @Schema(implementation = ProductoDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere rol ADMIN o EMPLEADO."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProductoDtoResponse> buscarId(@PathVariable Integer id) {

        return ResponseEntity.ok(productoService.buscarId(id));
    }
}
