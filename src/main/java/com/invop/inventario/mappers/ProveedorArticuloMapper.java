package com.invop.inventario.mappers;


import com.invop.inventario.dto.ProveedorArticuloDTO;
import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProveedorArticuloMapper {

    ProveedorArticuloMapper toDto(Proveedor proveedor);
    ProveedorArticuloDTO toEntity(Proveedor proveedor);

    List<ProveedorArticuloDTO> toDtoList(List<Proveedor> proveedor);
    List<ProveedorArticulo> toEntityList(List<ProveedorDTO> dtos);
}
