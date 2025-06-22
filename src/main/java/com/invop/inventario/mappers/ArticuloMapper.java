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

    @Mapping(target = "id", ignore = true)
    Articulo toEntity(ArticuloDatoDTO dto);

    @Mapping(target = "id", ignore = true)
    Articulo toEntityArticulo(ArticuloDTO dto);

    ArticuloDTO toArticuloDto(Articulo articulo);

    List<ArticuloDTO> toArticuloDtoList(List<Articulo> articulos);

    List<ArticuloDatoDTO> toDtoList(List<Articulo> articulo);

    @Mapping(target = "codArticulo", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateArticuloFromDto(EditarArticuloDTO dto, @MappingTarget Articulo articulo);
}

