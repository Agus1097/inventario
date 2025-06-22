package com.invop.inventario.mappers;

import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Proveedor;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ProveedorArticuloMapper.class,
        }, injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProveedorMapper {

    ProveedorDTO toDto(Proveedor proveedor);

    Proveedor toEntity(ProveedorDTO dto);

    List<ProveedorDTO> toDtoList(List<Proveedor> proveedores);

    List<Proveedor> toEntityList(List<ProveedorDTO> dtos);
}
