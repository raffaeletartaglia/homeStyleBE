package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.CategoriaRequestDTO;
import org.example.homestylebe.dto.response.CategoriaResponseDTO;
import org.example.homestylebe.entity.Categoria;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoriaMapper {
    CategoriaResponseDTO toDTO(Categoria entity);
    List<CategoriaResponseDTO> toDTOs(List<Categoria> entities);
    Categoria toEntity(CategoriaRequestDTO request);
    List<Categoria> toEntities(List<CategoriaRequestDTO> requests);

}
