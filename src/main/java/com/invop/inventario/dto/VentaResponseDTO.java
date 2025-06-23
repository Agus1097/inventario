package com.invop.inventario.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class VentaResponseDTO {
    private Long id;
    private Long articuloId;
    private String articuloNombre;
    private int cantidad;
    private float montoTotal; 
    private Date fechaVenta;
}