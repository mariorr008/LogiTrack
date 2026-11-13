package com.proyecto.LogiTrack.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;


    @PrePersist
    protected void generarId() {
        if (id == null) {
            id = ThreadLocalRandom.current().nextInt(100000, 1000000);
        }
    }
}