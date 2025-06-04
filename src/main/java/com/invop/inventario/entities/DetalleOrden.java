package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "detalles_ordenes")
public class DetalleOrden {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "cantidad_articulo")
    private int cantidadArticulo;

    @ManyToOne
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    @Column(name = "precio_unitario")
    private float precioUnitario;

    @Column(name = "precio_total")
    private float precioTotal;

    @ManyToOne
    @JoinColumn(name = "id_orden_compra")
    private OrdenCompra ordenCompra;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;
}
