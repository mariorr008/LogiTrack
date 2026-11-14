package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodegaRepository extends JpaRepository<Bodega, Integer> {

    boolean existsByNombre(String nombre);

    boolean existsByCiudadId(Integer ciudadId);

    boolean existsByNombreAndIdNot(String nombre, Integer id);

    boolean existsByEncargadoId(Integer encargadoId);

    boolean existsByEncargadoIdAndIdNot(Integer encargadoId, Integer id);


}