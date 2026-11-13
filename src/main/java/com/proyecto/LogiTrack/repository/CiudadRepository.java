package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
}
