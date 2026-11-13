package com.proyecto.LogiTrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "detalle_movimiento",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"movimiento_fk", "producto_fk"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleMovimiento extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movimiento_fk", nullable = false)
    private MovimientoInventario movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_fk", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;
}
