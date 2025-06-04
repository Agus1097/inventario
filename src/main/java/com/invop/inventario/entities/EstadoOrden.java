package com.invop.inventario.entities;

import lombok.Getter;

@Getter
public enum EstadoOrden {
    PENDIENTE(1L, "Pendiente"),
    ENVIADO(2L, "Enviado"),
    CANCELADO(3L, "Cancelado");

    private final Long id;
    private final String nombre;

    EstadoOrden(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}

