
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.ProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.ProductoDtoResponse;
import com.proyecto.LogiTrack.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoDtoResponse> guardar(
            @Valid @RequestBody ProductoDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoDtoRequest dto) {

        return ResponseEntity.ok(productoService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductoDtoResponse>> listar() {

        return ResponseEntity.ok(productoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> buscarId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(productoService.buscarId(id));
    }
}
