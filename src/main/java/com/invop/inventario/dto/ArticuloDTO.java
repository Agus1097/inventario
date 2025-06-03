package com.invop.inventario.dto;

import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.TipoModelo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloDTO {


    private Long id;
    private Long codArticulo;
    private String nombre;
    private String descripcion;
    private float demandaArticulo;
    private float costoAlmacenamiento;
    private float costoPedido;
    private float costoCompra;
    private LocalDate fechaBajaArticulo;
    private int puntoPedido;
    private int stockSeguridad;
    private int inventarioMaximo;
    private int loteOptimo;
    private int stockActual;

    private ProveedorDTO proveedorPredeterminado;

    private TipoModelo tipoModelo;
}
