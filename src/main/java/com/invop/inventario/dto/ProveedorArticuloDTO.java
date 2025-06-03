package com.invop.inventario.dto;

import com.invop.inventario.entities.Articulo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorArticuloDTO {


    private Long id;

    private int demoraEntrega;

    private float precioUnitario;

    private float cargosPedido;

    private List<ArticuloDTO> articulo = new ArrayList<>();

    private List<ProveedorDTO> proveedores = new ArrayList<>();


}
