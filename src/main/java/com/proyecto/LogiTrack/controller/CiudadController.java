
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.CiudadDtoRequest;
import com.proyecto.LogiTrack.dto.response.CiudadDtoResponse;
import com.proyecto.LogiTrack.service.CiudadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService ciudadService;

    @PostMapping
    public ResponseEntity<CiudadDtoResponse> guardar(
            @Valid @RequestBody CiudadDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ciudadService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CiudadDtoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CiudadDtoRequest dto) {

        return ResponseEntity.ok(ciudadService.actualizar(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        ciudadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CiudadDtoResponse>> listar() {

        return ResponseEntity.ok(ciudadService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadDtoResponse> buscarId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(ciudadService.buscarId(id));
    }
}
