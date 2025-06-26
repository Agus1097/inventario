package com.invop.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrarProveedorArticuloDTO {
    private Long idProveedor;
    private Long idArticulo;
}