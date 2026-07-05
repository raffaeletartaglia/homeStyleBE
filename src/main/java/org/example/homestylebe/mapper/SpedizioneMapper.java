package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.response.SpedizioneResponseDTO;
import org.example.homestylebe.entity.Spedizione;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpedizioneMapper {
    SpedizioneResponseDTO toDTO(Spedizione entity);
    List<SpedizioneResponseDTO> toDTOs(List<Spedizione> entities);

}
