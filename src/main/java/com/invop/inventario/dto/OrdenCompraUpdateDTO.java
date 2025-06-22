package com.invop.inventario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrdenCompraUpdateDTO {

    @JsonProperty("orden_compra_id")
    private Long ordenCompraId;

    private List<OrdenCompraArticuloDTO> detalles;
}
