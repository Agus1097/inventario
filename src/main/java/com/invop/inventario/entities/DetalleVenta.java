package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class DetalleVenta {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "cantidad_articulo")
    private int cantidadArticulo;

    @ManyToOne
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;
}
