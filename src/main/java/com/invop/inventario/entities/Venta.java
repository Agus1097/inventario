package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;


@Entity
@Table
@Data
public class Venta {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "fecha_venta")
    @Temporal(TemporalType.TIMESTAMP) // Opcional, especifica el tipo de fecha
    private Date fechaVenta;

    @ManyToOne
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    @Column
    private int cantidad;

    @Column(name = "monto_total")
    private float montoTotal;
}
