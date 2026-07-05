package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.response.MovimentoMagazzinoResponseDTO;
import org.example.homestylebe.entity.MovimentoMagazzino;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovimentoMagazzinoMapper {
    MovimentoMagazzinoResponseDTO toDTO(MovimentoMagazzino entity);
    List<MovimentoMagazzinoResponseDTO> toDTOs(List<MovimentoMagazzino> entities);

}
