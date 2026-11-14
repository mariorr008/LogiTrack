
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.CategoriaProductoDtoRequest;
import com.proyecto.LogiTrack.dto.response.CategoriaProductoDtoResponse;
import com.proyecto.LogiTrack.service.CategoriaProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaProductoDtoResponse> guardar(
            @Valid @RequestBody CategoriaProductoDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaProductoDtoRequest dto) {

        return ResponseEntity.ok(categoriaService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProductoDtoResponse>> listar() {

        return ResponseEntity.ok(categoriaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoDtoResponse> buscarId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(categoriaService.buscarId(id));
    }
}
