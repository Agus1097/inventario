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
//TODO: Agregar estos atributos a DTO faltantes
public class ArticuloDTO {
    private String nombre;
    private String descripcion;
    private Long codigo;
    private float costoAlmacenamiento;
    private float demanda;
    private float costoCompra;
    private int stockActual;
    private float costoVenta;
}
