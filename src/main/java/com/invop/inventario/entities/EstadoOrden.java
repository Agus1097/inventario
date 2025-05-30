package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class EstadoOrden {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String nombreEstado;
}
