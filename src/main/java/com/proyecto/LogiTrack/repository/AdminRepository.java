package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    @EntityGraph(attributePaths = "persona")
    Optional<Admin> findByPersona_Correo(String correo);
}