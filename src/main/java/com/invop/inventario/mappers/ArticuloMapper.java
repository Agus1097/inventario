package com.invop.inventario.mappers;


import com.invop.inventario.dto.ArticuloDTO;
import com.invop.inventario.dto.ArticuloDatoDTO;
import com.invop.inventario.dto.EditarArticuloDTO;
import com.invop.inventario.entities.Articulo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticuloMapper {

    ArticuloDatoDTO toDto(Articulo articulo);

    Articulo toEntity(ArticuloDatoDTO dto);

    Articulo toEntityArticulo(ArticuloDTO dto);

    ArticuloDTO toArticuloDto(Articulo articulo);

    List<ArticuloDTO> toArticuloDtoList(List<Articulo> articulos);

    List<ArticuloDatoDTO> toDtoList(List<Articulo> articulo);

    List<Articulo> toEntityList(List<ArticuloDatoDTO> dtos);

    @Mapping(target = "codArticulo", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateArticuloFromDto(EditarArticuloDTO dto, @MappingTarget Articulo articulo);
}

