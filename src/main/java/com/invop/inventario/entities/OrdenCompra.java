package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ordenes_compras")
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacionOrdenCompra;

    @Column(name = "fecha_modificacion")
    private LocalDate fechaModificacionOrdenCompra;

    @Column(name = "estado_orden")
    @Enumerated(EnumType.STRING)
    private EstadoOrden estadoOrden;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles;

    @Column(name = "monto_total")
    private float montoTotal;

    @ManyToOne
    private Proveedor proveedor;

}