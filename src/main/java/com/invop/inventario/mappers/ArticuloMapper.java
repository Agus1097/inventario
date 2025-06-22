package com.invop.inventario.mappers;


import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.ArticuloDatoDTO;
import com.invop.inventario.entities.Articulo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloMapper {

    ArticuloDatoDTO toDto(Articulo articulo);
    Articulo toEntity(ArticuloDatoDTO dto);
    Articulo toEntityArticulo(ArticuloDTO dto);

    List<ArticuloDatoDTO> toDtoList(List<Articulo> articulo);
    List<Articulo> toEntityList(List<ArticuloDatoDTO> dtos);
}
