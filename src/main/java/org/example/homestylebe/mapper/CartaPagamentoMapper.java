package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.CartaPagamentoRequestDTO;
import org.example.homestylebe.dto.response.CartaPagamentoResponseDTO;
import org.example.homestylebe.entity.CartaPagamento;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartaPagamentoMapper {
    CartaPagamentoResponseDTO toDTO(CartaPagamento entity);
    List<CartaPagamentoResponseDTO> toDTOs(List<CartaPagamento> entities);
    @org.mapstruct.Mapping(target = "scadenza", source = "scadenza", qualifiedByName = "yearMonthToLocalDate")
    @org.mapstruct.Mapping(target = "utente.id", source = "utenteId")
    CartaPagamento toEntity(CartaPagamentoRequestDTO request);

    List<CartaPagamento> toEntities(List<CartaPagamentoRequestDTO> requests);

    @org.mapstruct.Named("yearMonthToLocalDate")
    default java.time.LocalDate yearMonthToLocalDate(java.time.YearMonth ym) {
        if (ym == null) return null;
        return ym.atEndOfMonth();
    }

    @org.mapstruct.AfterMapping
    default void setUltime4Cifre(CartaPagamento entity, @org.mapstruct.MappingTarget CartaPagamentoResponseDTO dto) {
        if (entity.getNumeroCarta() != null && entity.getNumeroCarta().length() >= 4) {
            String num = entity.getNumeroCarta().replaceAll("\\s+", "");
            if (num.length() >= 4) {
                dto.setUltime4Cifre(num.substring(num.length() - 4));
            }
        }
        
        // Mappatura manuale sicura del tipo carta se serve
        if (entity.getTipoCarta() != null) {
            try {
                dto.setTipoCarta(CartaPagamentoResponseDTO.TipoCarta.valueOf(entity.getTipoCarta().toUpperCase()));
            } catch (IllegalArgumentException e) {
                dto.setTipoCarta(null);
            }
        }
    }
}
