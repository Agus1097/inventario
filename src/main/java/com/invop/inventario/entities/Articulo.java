package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table
@Data
public class Articulo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "cod_articulo")
    private Long codArticulo;

    private String nombre;

    private String descripcion;

    @Column(name = "demanda_articulo")
    private float demandaArticulo;

    @Column(name = "costo_almacenamiento")
    private float costoAlmacenamiento;

    @Column(name = "costo_pedido")
    private float costoPedido;

    @Column(name = "costo_compra")
    private float costoCompra;

    @Column(name = "fecha_baja_articulo")
    private LocalDate fechaBajaArticulo;

    @Column(name = "punto_pedido")
    private int puntoPedido;

    @Column(name = "stock_seguridad")
    private int stockSeguridad;

    @Column(name = "inventario_maximo")
    private int inventarioMaximo;

    @Column(name = "lote_optimo")
    private int loteOptimo;

    @Column(name = "stock_actual")
    private int stockActual;

    @ManyToOne
    @JoinColumn(name = "id_proveedor_predeterminado")
    private Proveedor proveedorPredeterminado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_modelo")
    private TipoModelo tipoModelo;
}
