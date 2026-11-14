package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.InventarioBodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventarioBodegaRepository extends JpaRepository<InventarioBodega, Integer> {

    List<InventarioBodega> findByBodegaId(Integer bodegaId);

    List<InventarioBodega> findByProductoId(Integer productoId);

    Optional<InventarioBodega> findByBodegaIdAndProductoId(
            Integer bodegaId, Integer productoId
    );
    /*    Ej:Queremos registrar una entrada de 30 unidades de arroz
          en la bodega 100001.
            1. Buscamos si ya existe el arroz en esa bodega.
            2. Si existe, actualizamos su stock.
            3. Si no existe, creamos un nuevo registro de inventario.
          Optional permite manejar ambos escenarios: que encuentre el
          registro o que no encuentre ninguno.
     */

    boolean existsByProductoId(Integer productoId);

    boolean existsByBodegaId(Integer bodegaId);

    @Query("SELECT COALESCE(SUM(i.stock), 0) FROM InventarioBodega i WHERE i.bodega.id = :bodegaId")
    Long sumarStockPorBodega(@Param("bodegaId") Integer bodegaId);


}