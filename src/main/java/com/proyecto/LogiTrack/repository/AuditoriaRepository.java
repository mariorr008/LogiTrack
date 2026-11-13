package com.proyecto.LogiTrack.repository;

import com.proyecto.LogiTrack.model.Auditoria;
import com.proyecto.LogiTrack.model.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findByUsuarioId(Integer usuarioId);

    List<Auditoria> findByTipoOperacion(TipoOperacion tipoOperacion);

    List<Auditoria> findByEntidadAfectada(String entidadAfectada);
}