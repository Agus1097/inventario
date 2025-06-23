package com.invop.inventario.dto;

import com.invop.inventario.entities.EstadoOrden;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrdenCompraDTO {

    private Long id;
    private LocalDate fechaCreacionOrdenCompra;
    private LocalDate fechaModificacionOrdenCompra;
    private EstadoOrden estadoOrden;
    private Long articuloId; 
    private String articuloNombre; 
    private float montoTotal;
    private int cantidad;
    private Long proveedorId; 
    private String proveedorNombre; 
}
