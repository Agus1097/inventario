package com.invop.inventario.dto;

import com.invop.inventario.entities.TipoModelo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorArticuloDTO {

    @Positive(message = "La demora de entrega debe ser mayor que 0")
    private int demoraEntrega;

    @Positive(message = "El precio unitario debe ser mayor que 0")
    private float precioUnitario;

    @Positive(message = "Los cargos del pedido deben ser mayores que 0")
    private float cargosPedido;

    @NotNull(message = "El artículo no puede ser nulo")
    private ArticuloDatoDTO articulo;

    @Positive(message = "El tiempo de revisión debe ser mayor que 0")
    private float tiempoRevision;

    @NotNull(message = "El tipo de modelo no puede ser nulo")
    private TipoModelo tipoModelo;
}
