package com.invop.inventario.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CrearOrdenCompraDTO {
    private Long articuloId;
    private Long proveedorId;
    private int cantidad;
}
