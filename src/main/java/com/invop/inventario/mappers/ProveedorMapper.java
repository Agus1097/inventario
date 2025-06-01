package com.invop.inventario.mappers;

import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Proveedor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper (componentModel = "spring")
public interface ProveedorMapper {
    ProveedorDTO toDto(Proveedor proveedor);
    Proveedor toEntity(ProveedorDTO dto);

    List<ProveedorDTO> toDtoList(List<Proveedor> proveedores);
    List<Proveedor> toEntityList(List<ProveedorDTO> dtos);
}
