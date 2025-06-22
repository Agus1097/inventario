package com.invop.inventario.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @Column(name = "cargos_pedido")
    private float cargosPedido;

    @ManyToOne
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    @Column(name = "tiempo_revision")
    private float tiempoRevision;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_modelo")
    private TipoModelo tipoModelo;
}
