package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {

    Optional<CategoriaProducto> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}