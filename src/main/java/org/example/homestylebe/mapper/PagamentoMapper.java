package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.PagamentoRequestDTO;
import org.example.homestylebe.dto.response.PagamentoResponseDTO;
import org.example.homestylebe.entity.Pagamento;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PagamentoMapper {
    PagamentoResponseDTO toDTO(Pagamento entity);
    List<PagamentoResponseDTO> toDTOs(List<Pagamento> entities);
    @org.mapstruct.Mapping(target = "ordine.id", source = "ordineId")
    @org.mapstruct.Mapping(target = "cartaPagamento.id", source = "cartaPagamentoId")
    Pagamento toEntity(PagamentoRequestDTO request);

    List<Pagamento> toEntities(List<PagamentoRequestDTO> requests);

}
