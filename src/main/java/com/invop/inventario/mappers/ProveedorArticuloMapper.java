package com.invop.inventario.mappers;

import com.invop.inventario.dto.ProveedorArticuloDTO;
import com.invop.inventario.dto.ProveedorDTO;
import com.invop.inventario.entities.Proveedor;
import com.invop.inventario.entities.ProveedorArticulo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ArticuloMapper.class,
        }, injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProveedorArticuloMapper {

    ProveedorArticuloDTO toDto(ProveedorArticulo proveedorArticulo);

    ProveedorArticulo toEntity(ProveedorArticuloDTO proveedorArticuloDTO);

    List<ProveedorArticuloDTO> toDtoList(List<ProveedorArticulo> proveedorArticulos);

    List<ProveedorArticulo> toEntityList(List<ProveedorArticuloDTO> dtos);
}
