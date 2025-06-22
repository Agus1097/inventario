package com.invop.inventario.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {

    @NotNull(message = "El nombre del proveedor no puede ser nulo")
    private String nombre;

    private List<ProveedorArticuloDTO> proveedorArticulos;
}
