package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.PrenotazioneRequestDTO;
import org.example.homestylebe.dto.response.PrenotazioneResponseDTO;
import org.example.homestylebe.entity.Prenotazione;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PrenotazioneMapper {
    PrenotazioneResponseDTO toDTO(Prenotazione entity);
    List<PrenotazioneResponseDTO> toDTOs(List<Prenotazione> entities);
    Prenotazione toEntity(PrenotazioneRequestDTO request);
    List<Prenotazione> toEntities(List<PrenotazioneRequestDTO> requests);

}
