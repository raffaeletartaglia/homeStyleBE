package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.ProdottoRequestDTO;
import org.example.homestylebe.dto.response.ProdottoResponseDTO;
import org.example.homestylebe.entity.Prodotto;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProdottoMapper {
    ProdottoResponseDTO toDTO(Prodotto entity);
    List<ProdottoResponseDTO> toDTOs(List<Prodotto> entities);
    @org.mapstruct.Mapping(source = "categoriaId", target = "categoria.id")
    Prodotto toEntity(ProdottoRequestDTO request);
    List<Prodotto> toEntities(List<ProdottoRequestDTO> requests);

}
