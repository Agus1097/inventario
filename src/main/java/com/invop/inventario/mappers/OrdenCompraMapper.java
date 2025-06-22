package com.invop.inventario.mappers;

import com.invop.inventario.dto.OrdenCompraDTO;
import com.invop.inventario.entities.OrdenCompra;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ArticuloMapper.class,
                ProveedorMapper.class
        },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrdenCompraMapper {

    OrdenCompraDTO toDto(OrdenCompra ordenCompra);

    OrdenCompra toEntity(OrdenCompraDTO dto);

    List<OrdenCompraDTO> toDtoList(List<OrdenCompra> ordenesCompra);

    List<OrdenCompra> toEntityList(List<OrdenCompraDTO> dtos);
}