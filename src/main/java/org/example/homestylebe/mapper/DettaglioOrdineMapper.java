package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.DettaglioOrdineRequestDTO;
import org.example.homestylebe.dto.response.DettaglioOrdineResponseDTO;
import org.example.homestylebe.entity.DettaglioOrdine;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DettaglioOrdineMapper {
    DettaglioOrdineResponseDTO toDTO(DettaglioOrdine entity);
    List<DettaglioOrdineResponseDTO> toDTOs(List<DettaglioOrdine> entities);
    DettaglioOrdine toEntity(DettaglioOrdineRequestDTO request);
    List<DettaglioOrdine> toEntities(List<DettaglioOrdineRequestDTO> requests);

}
