package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.UtenteRequestDTO;
import org.example.homestylebe.dto.response.UtenteResponseDTO;
import org.example.homestylebe.entity.Utente;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UtenteMapper {
    UtenteResponseDTO toDTO(Utente entity);
    List<UtenteResponseDTO> toDTOs(List<Utente> entities);
    Utente toEntity(UtenteRequestDTO request);
    List<Utente> toEntities(List<UtenteRequestDTO> requests);

}
