package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarcaRepository extends JpaRepository<Marca, Integer> {

    Optional<Marca> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}