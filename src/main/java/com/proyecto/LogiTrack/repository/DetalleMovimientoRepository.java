package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Integer> {

    List<DetalleMovimiento> findByMovimientoId(Integer movimientoId);

    List<DetalleMovimiento> findByProductoId(Integer productoId);

    boolean existsByProductoId(Integer productoId);

}