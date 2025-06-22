package com.invop.inventario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrdenCompraCreatedDTO {

    @JsonProperty("orden_compra_id")
    private Long ordenCompraId;
}
