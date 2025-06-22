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
    private Long articuloId; // Solo el ID en lugar del objeto completo
    private String articuloNombre; // Nombre del artículo para mostrar
    private float montoTotal;
    private int cantidad;
    private Long proveedorId; // Solo el ID en lugar del objeto completo
    private String proveedorNombre; // Nombre del proveedor para mostrar
}
