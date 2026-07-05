package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.CarrelloRequestDTO;
import org.example.homestylebe.dto.response.CarrelloResponseDTO;
import org.example.homestylebe.entity.Carrello;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarrelloMapper {
    CarrelloResponseDTO toDTO(Carrello entity);
    List<CarrelloResponseDTO> toDTOs(List<Carrello> entities);
    Carrello toEntity(CarrelloRequestDTO request);
    List<Carrello> toEntities(List<CarrelloRequestDTO> requests);

}
