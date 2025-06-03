package com.invop.inventario.mappers;


import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.entities.Articulo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloMapper {

    ArticuloDTO toDto(Articulo articulo);
    Articulo toEntity(ArticuloDTO dto);

    List<ArticuloDTO> toDtoList(List<Articulo> articulo);
    List<Articulo> toEntityList(List<ArticuloDTO> dtos);

}
