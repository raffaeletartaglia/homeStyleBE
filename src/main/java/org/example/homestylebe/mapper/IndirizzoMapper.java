package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.IndirizzoRequestDTO;
import org.example.homestylebe.dto.response.IndirizzoResponseDTO;
import org.example.homestylebe.entity.Indirizzo;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IndirizzoMapper {
    IndirizzoResponseDTO toDTO(Indirizzo entity);
    List<IndirizzoResponseDTO> toDTOs(List<Indirizzo> entities);
    Indirizzo toEntity(IndirizzoRequestDTO request);
    List<Indirizzo> toEntities(List<IndirizzoRequestDTO> requests);

}
