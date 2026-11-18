
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.response.AuditoriaDtoResponse;
import com.proyecto.LogiTrack.model.TipoOperacion;
import com.proyecto.LogiTrack.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<AuditoriaDtoResponse>> listar() {

        return ResponseEntity.ok(auditoriaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditoriaDtoResponse> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                auditoriaService.buscarPorId(id)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AuditoriaDtoResponse>> listarPorUsuario(
            @PathVariable Integer usuarioId) {

        return ResponseEntity.ok(
                auditoriaService.listarPorUsuario(usuarioId)
        );
    }

    @GetMapping("/tipo/{tipoOperacion}")
    public ResponseEntity<List<AuditoriaDtoResponse>> listarPorTipo(
            @PathVariable TipoOperacion tipoOperacion) {

        return ResponseEntity.ok(
                auditoriaService.listarPorTipo(tipoOperacion)
        );
    }

    @GetMapping("/entidad/{entidadAfectada}")
    public ResponseEntity<List<AuditoriaDtoResponse>> listarPorEntidad(
            @PathVariable String entidadAfectada) {

        return ResponseEntity.ok(
                auditoriaService.listarPorEntidad(entidadAfectada)
        );
    }
}
