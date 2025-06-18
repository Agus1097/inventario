package com.invop.inventario.entities;

import lombok.Getter;

@Getter
public enum EstadoOrden {
    PENDIENTE(1L, "Pendiente"),
    ENVIADO(2L, "Enviado"),
    FINALIZADO(3L, "Finalizado"),
    CANCELADO(4L, "Cancelado");

    private final Long id;
    private final String nombre;

    EstadoOrden(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}

