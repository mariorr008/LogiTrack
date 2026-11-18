
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.response.AuditoriaDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.model.TipoOperacion;
import com.proyecto.LogiTrack.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
@RequiredArgsConstructor
@Tag(name = "Auditorías", description = "Consulta de eventos registrados en la auditoría.")
@SecurityRequirement(name = "bearerAuth")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    @Operation(
            summary = "Listar auditorías",
            description = "Consulta todos los registros de auditoría. Requiere rol ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de auditorías.",
                    content = @Content(schema = @Schema(implementation = AuditoriaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere el rol ADMIN."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AuditoriaDtoResponse>> listar() {
        return ResponseEntity.ok(auditoriaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar auditoría por identificador",
            description = "Busca un registro de auditoría por su identificador. Requiere rol ADMIN."
    )
    @Parameter(name = "id", description = "Identificador de la auditoría.", example = "100001")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditoría encontrada.",
                    content = @Content(schema = @Schema(implementation = AuditoriaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere el rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Auditoría no encontrada.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuditoriaDtoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(auditoriaService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(
            summary = "Listar auditorías por usuario",
            description = "Consulta los registros de auditoría asociados a un usuario. Requiere rol ADMIN."
    )
    @Parameter(name = "usuarioId", description = "Identificador del usuario.", example = "100002")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditorías del usuario.",
                    content = @Content(schema = @Schema(implementation = AuditoriaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere el rol ADMIN."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AuditoriaDtoResponse>> listarPorUsuario(
            @PathVariable Integer usuarioId) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/tipo/{tipoOperacion}")
    @Operation(
            summary = "Listar auditorías por tipo de operación",
            description = "Tipos disponibles: INSERT, UPDATE y DELETE. Requiere rol ADMIN."
    )
    @Parameter(
            name = "tipoOperacion",
            description = "Tipo de operación auditada: INSERT, UPDATE o DELETE.",
            example = "INSERT",
            schema = @Schema(implementation = TipoOperacion.class)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditorías del tipo indicado.",
                    content = @Content(schema = @Schema(implementation = AuditoriaDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tipo de operación inválido.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere el rol ADMIN."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AuditoriaDtoResponse>> listarPorTipo(
            @PathVariable TipoOperacion tipoOperacion) {
        return ResponseEntity.ok(auditoriaService.listarPorTipo(tipoOperacion));
    }

    @GetMapping("/entidad/{entidadAfectada}")
    @Operation(
            summary = "Listar auditorías por entidad afectada",
            description = "Consulta los registros de una entidad específica. Requiere rol ADMIN."
    )
    @Parameter(
            name = "entidadAfectada",
            description = "Nombre de la entidad afectada.",
            example = "Producto"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditorías de la entidad.",
                    content = @Content(schema = @Schema(implementation = AuditoriaDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Se requiere el rol ADMIN."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<AuditoriaDtoResponse>> listarPorEntidad(
            @PathVariable String entidadAfectada) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidadAfectada));
    }
}
