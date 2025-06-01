package com.invop.inventario.dto;

import com.invop.inventario.entities.Articulo;
import jakarta.persistence.*;

public class ProveedorArticuloDTO {


    private Long id;

    private int demoraEntrega;

    private float precioUnitario;

    private float cargosPedido;

    private ArticuloDTO articulo;
}
