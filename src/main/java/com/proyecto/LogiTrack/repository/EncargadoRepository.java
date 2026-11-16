package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Encargado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface EncargadoRepository extends JpaRepository<Encargado, Integer> {
    @EntityGraph(attributePaths = "persona")
    Optional<Encargado> findByPersona_Correo(String correo);
}