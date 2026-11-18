package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.LogiTrack.dto.response.StockPorBodegaDtoResponse;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BodegaRepository extends JpaRepository<Bodega, Integer> {

    boolean existsByNombre(String nombre);

    boolean existsByCiudadId(Integer ciudadId);

    boolean existsByNombreAndIdNot(String nombre, Integer id);

    boolean existsByEncargadoId(Integer encargadoId);

    boolean existsByEncargadoIdAndIdNot(Integer encargadoId, Integer id);


    @Query("""
    SELECT new com.proyecto.LogiTrack.dto.response.StockPorBodegaDtoResponse(
        b.id,
        b.nombre,
        COALESCE(SUM(i.stock), 0)
    )
    FROM Bodega b
    LEFT JOIN InventarioBodega i ON i.bodega = b
    GROUP BY b.id, b.nombre
    ORDER BY b.nombre
""")
    List<StockPorBodegaDtoResponse> obtenerStockPorBodega();


}