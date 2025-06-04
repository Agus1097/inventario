package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table
public class TipoModelo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "nombre_tipo_modelo")
    private String nombreTipoModelo;
}
