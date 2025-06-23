package com.invop.inventario.mappers;

import com.invop.inventario.dto.OrdenCompraDTO;
import com.invop.inventario.entities.OrdenCompra;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(source = "articulo.id", target = "articuloId")
    @Mapping(source = "articulo.nombre", target = "articuloNombre")
    @Mapping(source = "proveedor.id", target = "proveedorId")
    @Mapping(source = "proveedor.nombre", target = "proveedorNombre")
    OrdenCompraDTO toDto(OrdenCompra ordenCompra);

    OrdenCompra toEntity(OrdenCompraDTO dto);

    List<OrdenCompraDTO> toDtoList(List<OrdenCompra> ordenesCompra);

    List<OrdenCompra> toEntityList(List<OrdenCompraDTO> dtos);
}