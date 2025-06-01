package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table
@Data
public class Articulo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long codArticulo;
    private String nombre;
    private String descripcion;
    private float demandaArticulo;
    private float costoAlmacenamiento;
    private float costoPedido;
    private float costoCompra;
    @Column(nullable = true)
    private LocalDate fechaBajaArticulo;
    @Column(nullable = true)
    private int puntoPedido;
    @Column(nullable = true)
    private int stockSeguridad;
    @Column(nullable = true)
    private int inventarioMaximo;
    @Column(nullable = true)
    private int loteOptimo;
    private int stockActual;

    @ManyToOne
    @JoinColumn(name = "id_proveedor_predeterminado", nullable = true)
    private Proveedor proveedorPredeterminado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_modelo")
    private TipoModelo tipoModelo;
}
