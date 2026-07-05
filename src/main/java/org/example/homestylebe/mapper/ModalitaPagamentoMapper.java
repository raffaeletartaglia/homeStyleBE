package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.ModalitaPagamentoRequestDTO;
import org.example.homestylebe.dto.response.ModalitaPagamentoResponseDTO;
import org.example.homestylebe.entity.ModalitaPagamento;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModalitaPagamentoMapper {
    ModalitaPagamentoResponseDTO toDTO(ModalitaPagamento entity);
    List<ModalitaPagamentoResponseDTO> toDTOs(List<ModalitaPagamento> entities);
    ModalitaPagamento toEntity(ModalitaPagamentoRequestDTO request);
    List<ModalitaPagamento> toEntities(List<ModalitaPagamentoRequestDTO> requests);

}
