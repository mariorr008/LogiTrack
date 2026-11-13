package com.proyecto.LogiTrack.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "encargado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Encargado {

    @jakarta.persistence.Id
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private Persona persona;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "debe_cambiar_password", nullable = false)
    private Boolean debeCambiarPassword = true;
}