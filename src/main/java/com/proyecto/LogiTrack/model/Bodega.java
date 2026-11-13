package com.proyecto.LogiTrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bodega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bodega extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ciudad_fk", nullable = false)
    private Ciudad ciudad;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false)
    private Integer capacidad;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargado_fk", unique = true)
    private Encargado encargado;
}