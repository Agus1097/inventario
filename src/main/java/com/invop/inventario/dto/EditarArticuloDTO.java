package com.invop.inventario.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarArticuloDTO {

    private String nombre;

    private String descripcion;

    @PositiveOrZero(message = "La producción diaria no puede ser negativa.")
    private float produccionDiaria;

    @PositiveOrZero(message = "La demanda del artículo no puede ser negativa.")
    private float demandaArticulo;

    @PositiveOrZero(message = "El costo de almacenamiento no puede ser negativo.")
    private float costoAlmacenamiento;

    @PositiveOrZero(message = "El costo de venta no puede ser negativo.")
    private float costoVenta;

    @PositiveOrZero(message = "El stock actual no puede ser negativo.")
    private int stockActual;

    private float z;

    @PositiveOrZero(message = "La desviación estándar no puede ser negativa.")
    private float desviacionEstandar;
}

