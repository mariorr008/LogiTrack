
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.InventarioBodegaDtoRequest;
import com.proyecto.LogiTrack.dto.response.InventarioBodegaDtoResponse;
import com.proyecto.LogiTrack.service.InventarioBodegaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventarios")
@RequiredArgsConstructor
public class InventarioBodegaController {

    private final InventarioBodegaService inventarioService;

    @PostMapping
    public ResponseEntity<InventarioBodegaDtoResponse> guardar(
            @Valid @RequestBody InventarioBodegaDtoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.guardar(dto));
    }

    @GetMapping
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listar() {

        return ResponseEntity.ok(inventarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioBodegaDtoResponse> buscarId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(inventarioService.buscarId(id));
    }

    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listarPorBodega(
            @PathVariable Integer bodegaId) {

        return ResponseEntity.ok(
                inventarioService.listarPorBodega(bodegaId));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listarPorProducto(
            @PathVariable Integer productoId) {

        return ResponseEntity.ok(
                inventarioService.listarPorProducto(productoId));
    }

    // Consultar productos con menos de 10 unidades.
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventarioBodegaDtoResponse>> listarStockBajo() {

        return ResponseEntity.ok(inventarioService.listarStockBajo());
    }
}
