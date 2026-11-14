
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.MovimientoInventarioDtoRequest;
import com.proyecto.LogiTrack.dto.response.MovimientoInventarioDtoResponse;
import com.proyecto.LogiTrack.mapper.MovimientoInventarioMapper;
import com.proyecto.LogiTrack.model.*;
import com.proyecto.LogiTrack.repository.*;
import com.proyecto.LogiTrack.service.MovimientoInventarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioServiceImpl
        implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final DetalleMovimientoRepository detalleRepository;
    private final InventarioBodegaRepository inventarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final PersonaRepository personaRepository;
    private final MovimientoInventarioMapper mapper;

    @Override
    @Transactional
    public MovimientoInventarioDtoResponse registrar(
            MovimientoInventarioDtoRequest dto) {

        validarDetalles(dto);

        Persona responsable = personaRepository.findById(
                dto.getUsuarioResponsableId()
        ).orElseThrow(() ->
                new IllegalArgumentException("El usuario responsable no existe"));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setUsuarioResponsable(responsable);

        List<InventarioBodega> inventariosDestino = new ArrayList<>();
        List<Producto> productos = new ArrayList<>();

        Bodega origen = null;
        Bodega destino = null;

        if (dto.getTipoMovimiento() == TipoMovimiento.ENTRADA) {

            validarEntrada(dto);

            destino = buscarBodega(dto.getBodegaDestinoId());
            validarCapacidad(destino, dto);

            for (var detalle : dto.getDetalles()) {
                Producto producto = buscarProducto(detalle.getProductoId());
                productos.add(producto);

                InventarioBodega inventario =
                        obtenerInventarioDestino(destino, producto);

                validarInventarioActivo(inventario);
                inventariosDestino.add(inventario);
            }

            movimiento.setBodegaDestino(destino);

        } else if (dto.getTipoMovimiento() == TipoMovimiento.SALIDA) {

            validarSalida(dto);

            origen = buscarBodega(dto.getBodegaOrigenId());

            for (var detalle : dto.getDetalles()) {
                Producto producto = buscarProducto(detalle.getProductoId());
                productos.add(producto);

                InventarioBodega inventario = inventarioRepository
                        .findByBodegaIdAndProductoId(
                                origen.getId(), producto.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El producto no existe en la bodega de origen"
                                ));

                validarInventarioActivo(inventario);
                validarStock(inventario, detalle.getCantidad());

                inventariosDestino.add(inventario);
            }

            movimiento.setBodegaOrigen(origen);

        } else if (dto.getTipoMovimiento() == TipoMovimiento.TRANSFERENCIA) {

            validarTransferencia(dto);

            origen = buscarBodega(dto.getBodegaOrigenId());
            destino = buscarBodega(dto.getBodegaDestinoId());

            if (origen.getId().equals(destino.getId())) {
                throw new IllegalArgumentException(
                        "La bodega de origen y destino deben ser diferentes"
                );
            }

            validarCapacidad(destino, dto);

            for (var detalle : dto.getDetalles()) {
                Producto producto = buscarProducto(detalle.getProductoId());
                productos.add(producto);

                InventarioBodega inventarioOrigen = inventarioRepository
                        .findByBodegaIdAndProductoId(
                                origen.getId(), producto.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El producto no existe en la bodega de origen"
                                ));

                validarInventarioActivo(inventarioOrigen);
                validarStock(inventarioOrigen, detalle.getCantidad());

                InventarioBodega inventarioDestino =
                        obtenerInventarioDestino(destino, producto);

                validarInventarioActivo(inventarioDestino);

                inventariosDestino.add(inventarioDestino);
            }

            movimiento.setBodegaOrigen(origen);
            movimiento.setBodegaDestino(destino);

        } else {
            throw new IllegalArgumentException("Tipo de movimiento inválido");
        }

        movimiento = movimientoRepository.save(movimiento);

        List<DetalleMovimiento> detallesGuardados = new ArrayList<>();

        for (int i = 0; i < dto.getDetalles().size(); i++) {

            var detalleDto = dto.getDetalles().get(i);
            Producto producto = productos.get(i);
            InventarioBodega inventario = inventariosDestino.get(i);

            if (dto.getTipoMovimiento() == TipoMovimiento.ENTRADA) {

                inventario.setStock(
                        inventario.getStock() + detalleDto.getCantidad()
                );

                inventario.setEstado(EstadoInventario.ACTIVO);

                inventarioRepository.save(inventario);

            } else if (dto.getTipoMovimiento() == TipoMovimiento.SALIDA) {

                inventario.setStock(
                        inventario.getStock() - detalleDto.getCantidad()
                );

                inventario.setEstado(
                        inventario.getStock() == 0
                                ? EstadoInventario.AGOTADO
                                : EstadoInventario.ACTIVO
                );

                inventarioRepository.save(inventario);

            } else {

                InventarioBodega inventarioOrigen = inventarioRepository
                        .findByBodegaIdAndProductoId(
                                origen.getId(), producto.getId()
                        )
                        .orElseThrow();

                inventarioOrigen.setStock(
                        inventarioOrigen.getStock() - detalleDto.getCantidad()
                );

                inventarioOrigen.setEstado(
                        inventarioOrigen.getStock() == 0
                                ? EstadoInventario.AGOTADO
                                : EstadoInventario.ACTIVO
                );

                inventario.setStock(
                        inventario.getStock() + detalleDto.getCantidad()
                );

                inventario.setEstado(EstadoInventario.ACTIVO);

                inventarioRepository.save(inventarioOrigen);
                inventarioRepository.save(inventario);
            }

            DetalleMovimiento detalle = new DetalleMovimiento();
            detalle.setMovimiento(movimiento);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.getCantidad());

            detallesGuardados.add(detalleRepository.save(detalle));
        }

        return mapper.entityToDto(movimiento, detallesGuardados);
    }

    private void validarDetalles(MovimientoInventarioDtoRequest dto) {

        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El movimiento debe contener al menos un producto"
            );
        }

        Set<Integer> productos = new HashSet<>();

        for (var detalle : dto.getDetalles()) {

            if (detalle.getProductoId() == null
                    || detalle.getCantidad() == null
                    || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "Los productos y cantidades deben ser válidos"
                );
            }

            if (!productos.add(detalle.getProductoId())) {
                throw new IllegalArgumentException(
                        "No se puede repetir un producto en el mismo movimiento"
                );
            }
        }
    }

    private void validarEntrada(MovimientoInventarioDtoRequest dto) {

        if (dto.getBodegaOrigenId() != null
                || dto.getBodegaDestinoId() == null) {
            throw new IllegalArgumentException(
                    "Una entrada requiere únicamente una bodega destino"
            );
        }
    }

    private void validarSalida(MovimientoInventarioDtoRequest dto) {

        if (dto.getBodegaOrigenId() == null
                || dto.getBodegaDestinoId() != null) {
            throw new IllegalArgumentException(
                    "Una salida requiere únicamente una bodega origen"
            );
        }
    }

    private void validarTransferencia(MovimientoInventarioDtoRequest dto) {

        if (dto.getBodegaOrigenId() == null
                || dto.getBodegaDestinoId() == null) {
            throw new IllegalArgumentException(
                    "Una transferencia requiere bodega origen y destino"
            );
        }
    }

    private Bodega buscarBodega(Integer id) {

        return bodegaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("La bodega no existe"));
    }

    private Producto buscarProducto(Integer id) {

        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("El producto no existe"));
    }

    private InventarioBodega obtenerInventarioDestino(
            Bodega bodega, Producto producto) {

        return inventarioRepository
                .findByBodegaIdAndProductoId(
                        bodega.getId(), producto.getId()
                )
                .orElseGet(() -> {
                    InventarioBodega inventario = new InventarioBodega();
                    inventario.setBodega(bodega);
                    inventario.setProducto(producto);
                    inventario.setStock(0);
                    inventario.setEstado(EstadoInventario.AGOTADO);
                    return inventario;
                });
    }

    private void validarInventarioActivo(InventarioBodega inventario) {

        if (inventario.getEstado() == EstadoInventario.INACTIVO) {
            throw new IllegalStateException(
                    "No se pueden realizar movimientos sobre inventarios inactivos"
            );
        }
    }

    private void validarStock(
            InventarioBodega inventario, Integer cantidad) {

        if (inventario.getStock() < cantidad) {
            throw new IllegalStateException(
                    "Stock insuficiente para realizar el movimiento"
            );
        }
    }

    private void validarCapacidad(
            Bodega bodega, MovimientoInventarioDtoRequest dto) {

        Long stockActual = inventarioRepository.sumarStockPorBodega(
                bodega.getId()
        );

        long cantidadEntrante = dto.getDetalles().stream()
                .mapToLong(detalle -> detalle.getCantidad())
                .sum();

        if (stockActual + cantidadEntrante > bodega.getCapacidad()) {
            throw new IllegalStateException(
                    "La capacidad de la bodega será superada"
            );
        }
    }

    @Override
    public List<MovimientoInventarioDtoResponse> listar() {

        return movimientoRepository.findAll()
                .stream()
                .map(movimiento -> mapper.entityToDto(
                        movimiento,
                        detalleRepository.findByMovimientoId(movimiento.getId())
                ))
                .toList();
    }

    @Override
    public MovimientoInventarioDtoResponse buscarId(Integer id) {

        MovimientoInventario movimiento = movimientoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("El movimiento no existe"));

        return mapper.entityToDto(
                movimiento,
                detalleRepository.findByMovimientoId(id)
        );
    }

    @Override
    public List<MovimientoInventarioDtoResponse> listarPorUsuario(
            Integer usuarioId) {

        return movimientoRepository.findByUsuarioResponsableId(usuarioId)
                .stream()
                .map(movimiento -> mapper.entityToDto(
                        movimiento,
                        detalleRepository.findByMovimientoId(movimiento.getId())
                ))
                .toList();
    }

    @Override
    public List<MovimientoInventarioDtoResponse> listarPorTipo(
            TipoMovimiento tipo) {

        return movimientoRepository.findByTipoMovimiento(tipo)
                .stream()
                .map(movimiento -> mapper.entityToDto(
                        movimiento,
                        detalleRepository.findByMovimientoId(movimiento.getId())
                ))
                .toList();
    }
}
