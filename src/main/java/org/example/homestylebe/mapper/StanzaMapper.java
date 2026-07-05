package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.StanzaRequestDTO;
import org.example.homestylebe.dto.response.StanzaResponseDTO;
import org.example.homestylebe.entity.Stanza;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StanzaMapper {
    StanzaResponseDTO toDTO(Stanza entity);
    List<StanzaResponseDTO> toDTOs(List<Stanza> entities);
    Stanza toEntity(StanzaRequestDTO request);
    List<Stanza> toEntities(List<StanzaRequestDTO> requests);

}
