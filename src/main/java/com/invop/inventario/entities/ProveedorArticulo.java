package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "proveedor_articulo")
public class ProveedorArticulo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "demora_entrega")
    private int demoraEntrega;

    @Column(name = "precio_unitario")
    private float precioUnitario;

    @Column(name = "cargos_pedido")
    private float cargosPedido;

    //TODO: revisar
    @ManyToOne
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    @Column(name = "tiempo_revision")
    private float tiempoRevision;

    @ManyToOne
    @JoinColumn(name = "id_tipo_modelo")
    private TipoModelo tipoModelo;

}
