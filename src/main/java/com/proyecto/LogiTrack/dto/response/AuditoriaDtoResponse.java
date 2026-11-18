
package com.proyecto.LogiTrack.dto.response;

import com.proyecto.LogiTrack.model.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDtoResponse {

    private Integer id;
    private LocalDateTime fechaHora;
    private TipoOperacion tipoOperacion;
    private Integer usuarioId;
    private String entidadAfectada;
    private Integer registroAfectadoId;
    private String valoresAnteriores;
    private String valoresNuevos;
}
