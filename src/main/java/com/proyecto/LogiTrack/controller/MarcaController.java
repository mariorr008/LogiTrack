
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.MarcaDtoRequest;
import com.proyecto.LogiTrack.dto.response.MarcaDtoResponse;
import com.proyecto.LogiTrack.service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @PostMapping
    public ResponseEntity<MarcaDtoResponse> guardar(
            @Valid @RequestBody MarcaDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(marcaService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarcaDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MarcaDtoRequest dto) {

        return ResponseEntity.ok(marcaService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        marcaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MarcaDtoResponse>> listar() {

        return ResponseEntity.ok(marcaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcaDtoResponse> buscarId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(marcaService.buscarId(id));
    }
}
