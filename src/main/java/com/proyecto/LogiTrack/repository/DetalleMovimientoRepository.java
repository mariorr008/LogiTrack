package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.LogiTrack.dto.response.ProductoMasMovidoDtoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Integer> {

    List<DetalleMovimiento> findByMovimientoId(Integer movimientoId);

    List<DetalleMovimiento> findByProductoId(Integer productoId);

    boolean existsByProductoId(Integer productoId);

    @Query("""
    SELECT new com.proyecto.LogiTrack.dto.response.ProductoMasMovidoDtoResponse(
        p.id,
        p.nombre,
        SUM(d.cantidad)
    )
    FROM DetalleMovimiento d
    JOIN d.producto p
    GROUP BY p.id, p.nombre
    ORDER BY SUM(d.cantidad) DESC
""")
    List<ProductoMasMovidoDtoResponse> obtenerProductosMasMovidos(
            Pageable pageable
    );

}