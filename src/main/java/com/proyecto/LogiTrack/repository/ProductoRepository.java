package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Integer id);

    boolean existsByMarcaId(Integer marcaId);

    boolean existsByCategoriaId(Integer categoriaId);
}