package com.invop.inventario.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrdenCompraArticuloDTO {

    private int cantidadArticulo;

    private Long codArticulo;
}
