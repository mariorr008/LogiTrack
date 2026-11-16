
package com.proyecto.LogiTrack.service.impl;

import com.proyecto.LogiTrack.dto.request.MovimientoInventarioDtoRequest;
import com.proyecto.LogiTrack.dto.response.MovimientoInventarioDtoResponse;
import com.proyecto.LogiTrack.exception.BusinessRuleException;
import com.proyecto.LogiTrack.exception.ResourceNotFoundException;
import com.proyecto.LogiTrack.mapper.MovimientoInventarioMapper;
import com.proyecto.LogiTrack.model.*;
import com.proyecto.LogiTrack.repository.*;
import com.proyecto.LogiTrack.service.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
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
    public MovimientoInventarioDtoResponse registrar(
            MovimientoInventarioDtoRequest dto) {

        validarDetalles(dto);

        Persona responsable = personaRepository.findById(
                dto.getUsuarioResponsableId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "No se encontró el usuario responsable con ID: "
                                + dto.getUsuarioResponsableId()
                ));

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
                                new BusinessRuleException(
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
                throw new BusinessRuleException(
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
                                new BusinessRuleException(
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
            throw new BusinessRuleException("Tipo de movimiento inválido");
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
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No se encontró el inventario del producto en la bodega de origen"
                                ));

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
            throw new BusinessRuleException(
                    "El movimiento debe contener al menos un producto"
            );
        }

        Set<Integer> productos = new HashSet<>();

        for (var detalle : dto.getDetalles()) {

            if (detalle.getProductoId() == null
                    || detalle.getCantidad() == null
                    || detalle.getCantidad() <= 0) {
                throw new BusinessRuleException(
                        "Los productos y cantidades deben ser válidos"
                );
            }

            if (!productos.add(detalle.getProductoId())) {
                throw new BusinessRuleException(
                        "No se puede repetir un producto en el mismo movimiento"
                );
            }
        }
    }

    private void validarEntrada(MovimientoInventarioDtoRequest dto) {

        if (dto.getBodegaOrigenId() != null
                || dto.getBodegaDestinoId() == null) {
            throw new BusinessRuleException(
                    "Una entrada requiere únicamente una bodega destino"
            );
        }
    }

    private void validarSalida(MovimientoInventarioDtoRequest dto) {

        if (dto.getBodegaOrigenId() == null
                || dto.getBodegaDestinoId() != null) {
            throw new BusinessRuleException(
                    "Una salida requiere únicamente una bodega origen"
            );
        }
    }

    private void validarTransferencia(MovimientoInventarioDtoRequest dto) {

        if (dto.getBodegaOrigenId() == null
                || dto.getBodegaDestinoId() == null) {
            throw new BusinessRuleException(
                    "Una transferencia requiere bodega origen y destino"
            );
        }
    }

    private Bodega buscarBodega(Integer id) {

        return bodegaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró la bodega con ID: " + id
                        ));
    }

    private Producto buscarProducto(Integer id) {

        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el producto con ID: " + id
                        ));
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
            throw new BusinessRuleException(
                    "No se pueden realizar movimientos sobre inventarios inactivos"
            );
        }
    }

    private void validarStock(
            InventarioBodega inventario, Integer cantidad) {

        if (inventario.getStock() < cantidad) {
            throw new BusinessRuleException(
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
            throw new BusinessRuleException(
                    "La capacidad de la bodega será superada"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public MovimientoInventarioDtoResponse buscarId(Integer id) {

        MovimientoInventario movimiento = movimientoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No se encontró el movimiento con ID: " + id
                        ));

        return mapper.entityToDto(
                movimiento,
                detalleRepository.findByMovimientoId(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDtoResponse> listarPorUsuario(
            Integer usuarioId) {

        if (!personaRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException(
                    "No se encontró la persona con ID: " + usuarioId
            );
        }

        return movimientoRepository.findByUsuarioResponsableId(usuarioId)
                .stream()
                .map(movimiento -> mapper.entityToDto(
                        movimiento,
                        detalleRepository.findByMovimientoId(movimiento.getId())
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDtoResponse> listarPorTipo(
            TipoMovimiento tipo) {

        if (tipo == null) {
            throw new BusinessRuleException(
                    "El tipo de movimiento es obligatorio"
            );
        }

        return movimientoRepository.findByTipoMovimiento(tipo)
                .stream()
                .map(movimiento -> mapper.entityToDto(
                        movimiento,
                        detalleRepository.findByMovimientoId(movimiento.getId())
                ))
                .toList();
    }
}
