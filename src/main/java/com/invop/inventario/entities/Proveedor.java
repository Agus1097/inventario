package com.invop.inventario.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "proveedores")
public class Proveedor {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefono", nullable = false)
    private String phone;

    @Column(name = "direccion", nullable = false)
    private String address;

    @Column(name = "cuit", nullable = false)
    private String cuit;

    //@Column(name = "articulo")
    //private Articulo articulo;
}
