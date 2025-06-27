package com.invop.inventario.dto;

import lombok.Data;

@Data
public class ArticuloOrdenDTO {
    private Long idArticulo;
    private Long codArticulo;
    private String nombreArticulo;
    private Long idProveedorPredeterminado;
    private String nombreProveedorPredeterminado;
    private Integer loteOptimo;
}