
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.MovimientoInventarioDtoRequest;
import com.proyecto.LogiTrack.dto.response.MovimientoInventarioDtoResponse;
import com.proyecto.LogiTrack.model.TipoMovimiento;
import com.proyecto.LogiTrack.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    @PostMapping
    public ResponseEntity<MovimientoInventarioDtoResponse> registrar(
            @Valid @RequestBody MovimientoInventarioDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioDtoResponse>> listar() {

        return ResponseEntity.ok(movimientoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioDtoResponse> buscarId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(movimientoService.buscarId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MovimientoInventarioDtoResponse>> listarPorUsuario(
            @PathVariable Integer usuarioId) {

        return ResponseEntity.ok(
                movimientoService.listarPorUsuario(usuarioId)
        );
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimientoInventarioDtoResponse>> listarPorTipo(
            @PathVariable TipoMovimiento tipo) {

        return ResponseEntity.ok(
                movimientoService.listarPorTipo(tipo)
        );
    }
}
