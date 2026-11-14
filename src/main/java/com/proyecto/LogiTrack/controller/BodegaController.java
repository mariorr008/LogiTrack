
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.BodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.BodegaDtoResponse;
import com.proyecto.LogiTrack.service.BodegaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bodegas")
@RequiredArgsConstructor
public class BodegaController {

    private final BodegaService bodegaService;

    @PostMapping
    public ResponseEntity<BodegaDtoResponse> guardar(
            @Valid @RequestBody BodegaDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bodegaService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BodegaDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody BodegaDtoRequest dto) {

        return ResponseEntity.ok(bodegaService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        bodegaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BodegaDtoResponse>> listar() {

        return ResponseEntity.ok(bodegaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BodegaDtoResponse> buscarId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(bodegaService.buscarId(id));
    }
}
