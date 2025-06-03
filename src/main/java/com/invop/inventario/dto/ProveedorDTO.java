package com.invop.inventario.dto;


import com.invop.inventario.entities.ProveedorArticulo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {


        private Long id;

        private BigDecimal costoAlmacenamiento;

        private BigDecimal costoArticulo;

        private String descripcionArticulo;

        private int stockArticulo;

        private List<ProveedorArticuloDTO> articulosProveedor = new ArrayList<>();


}
