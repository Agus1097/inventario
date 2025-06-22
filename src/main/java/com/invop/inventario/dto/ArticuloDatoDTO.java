package com.invop.inventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloDatoDTO {

    @NotNull(message = "El ID del artículo no puede ser nulo")
    private Long id;

    @NotNull(message = "El nombre del artículo no puede ser nulo")
    private String nombre;

    @NotNull(message = "El código del artículo no puede ser nulo")
    private Long codArticulo;
}