package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.OrdineRequestDTO;
import org.example.homestylebe.dto.response.OrdineResponseDTO;
import org.example.homestylebe.entity.Ordine;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {DettaglioOrdineMapper.class, SpedizioneMapper.class, UtenteMapper.class, IndirizzoMapper.class, PagamentoMapper.class})
public interface OrdineMapper {
    @org.mapstruct.Mapping(source = "dettagliOrdine", target = "prodotti")
    OrdineResponseDTO toDTO(Ordine entity);
    List<OrdineResponseDTO> toDTOs(List<Ordine> entities);
    Ordine toEntity(OrdineRequestDTO request);
    List<Ordine> toEntities(List<OrdineRequestDTO> requests);

}
