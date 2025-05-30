package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class DetalleOrden {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private int cantidadArticulo;

    @ManyToOne
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;
}
