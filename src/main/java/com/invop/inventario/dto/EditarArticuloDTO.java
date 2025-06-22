package com.invop.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarArticuloDTO {
    private String nombre;
    private String descripcion;
    private Integer demanda;
    private Float costoAlmacenamiento;
    private Float costoCompra;
    private int stockActual;
    private float costoVenta;
}

