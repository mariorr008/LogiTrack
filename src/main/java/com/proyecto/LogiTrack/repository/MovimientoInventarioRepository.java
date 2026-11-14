package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.MovimientoInventario;
import com.proyecto.LogiTrack.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByUsuarioResponsableId(Integer usuarioId);

    List<MovimientoInventario> findByTipoMovimiento(
            TipoMovimiento tipoMovimiento
    );

    boolean existsByBodegaOrigenId(Integer bodegaId);

    boolean existsByBodegaDestinoId(Integer bodegaId);

}