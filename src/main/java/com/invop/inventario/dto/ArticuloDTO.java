package com.invop.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloDTO {

    private Long id;

    @NotNull(message = "El código del artículo no puede ser nulo.")
    private Long codArticulo;

    @NotBlank(message = "El nombre del artículo no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "La descripción del artículo no puede estar vacía.")
    private String descripcion;

    @NotNull(message = "La producción diaria no puede ser nula.")
    @PositiveOrZero(message = "La producción diaria no puede ser negativa.")
    private float produccionDiaria;

    @NotNull(message = "La demanda del artículo no puede ser nula.")
    @PositiveOrZero(message = "La demanda del artículo no puede ser negativa.")
    private float demandaArticulo;

    @NotNull(message = "El costo de almacenamiento no puede ser nulo.")
    @PositiveOrZero(message = "El costo de almacenamiento no puede ser negativo.")
    private float costoAlmacenamiento;

    @NotNull(message = "El costo de venta no puede ser nulo.")
    @PositiveOrZero(message = "El costo de venta no puede ser negativo.")
    private float costoVenta;

    @NotNull(message = "El stock actual no puede ser nulo.")
    @PositiveOrZero(message = "El stock actual no puede ser negativo.")
    private int stockActual;

    @NotNull(message = "El valor de Z no puede ser nulo.")
    private float z;

    @NotNull(message = "La desviación estándar no puede ser nula.")
    @PositiveOrZero(message = "La desviación estándar no puede ser negativa.")
    private float desviacionEstandar;

    private ProveedorDTO proveedorPredeterminado;

    private LocalDate fechaBajaArticulo;

    private int stockSeguridad;

    private int loteOptimo;

    private int inventarioMaximo;

    private int puntoPedido;

    private float cgi;
}
