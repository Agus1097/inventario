package com.invop.inventario.dto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {


        private Long id;

        private BigDecimal costoAlmacenamiento;

        private BigDecimal costoArticulo;

        private String descripcionArticulo;

        private int stockArticulo;

}
