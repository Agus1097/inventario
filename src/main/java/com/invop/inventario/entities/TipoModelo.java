package com.invop.inventario.entities;

import lombok.Getter;

@Getter
public enum TipoModelo {

    LOTE_FIJO(1L, "Lote Fijo"),
    INTERVALO_FIJO(2L, "Intervalo Fijo"),;

    private final Long id;
    private final String nombre;

    TipoModelo(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
