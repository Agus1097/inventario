package com.invop.inventario.mappers;


import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.entities.Articulo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloMapper {

    Articulo toDto(Articulo articulo);
    ArticuloDTO toEntity(ArticuloDTO dto);

    List<ArticuloDTO> toDto(List<Articulo> articulo);
    List<Articulo> toEntity(List<ArticuloDTO> dtos);

}
