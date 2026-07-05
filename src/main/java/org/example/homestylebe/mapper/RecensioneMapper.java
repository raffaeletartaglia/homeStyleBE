package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.RecensioneRequestDTO;
import org.example.homestylebe.dto.response.RecensioneResponseDTO;
import org.example.homestylebe.entity.Recensione;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecensioneMapper {
    RecensioneResponseDTO toDTO(Recensione entity);
    List<RecensioneResponseDTO> toDTOs(List<Recensione> entities);
    Recensione toEntity(RecensioneRequestDTO request);
    List<Recensione> toEntities(List<RecensioneRequestDTO> requests);

}
