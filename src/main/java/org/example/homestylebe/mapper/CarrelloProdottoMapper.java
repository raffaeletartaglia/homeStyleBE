package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.CarrelloProdottoRequestDTO;
import org.example.homestylebe.dto.response.CarrelloProdottoResponseDTO;
import org.example.homestylebe.entity.CarrelloProdotto;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarrelloProdottoMapper {
    CarrelloProdottoResponseDTO toDTO(CarrelloProdotto entity);
    List<CarrelloProdottoResponseDTO> toDTOs(List<CarrelloProdotto> entities);
    @org.mapstruct.Mapping(target = "prodotto.id", source = "prodottoId")
    @org.mapstruct.Mapping(target = "carrello.id", source = "carrelloId")
    CarrelloProdotto toEntity(CarrelloProdottoRequestDTO request);
    List<CarrelloProdotto> toEntities(List<CarrelloProdottoRequestDTO> requests);

}
