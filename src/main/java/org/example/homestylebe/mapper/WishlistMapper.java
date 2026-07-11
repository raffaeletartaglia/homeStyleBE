package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.WishlistRequestDTO;
import org.example.homestylebe.dto.response.WishlistResponseDTO;
import org.example.homestylebe.entity.Wishlist;


import org.mapstruct.Mapping;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ProdottoMapper.class})
public interface WishlistMapper {
    @Mapping(source = "utente.id", target = "utenteId")
    WishlistResponseDTO toDTO(Wishlist entity);
    
    List<WishlistResponseDTO> toDTOs(List<Wishlist> entities);
    
    Wishlist toEntity(WishlistRequestDTO request);
    List<Wishlist> toEntities(List<WishlistRequestDTO> requests);
}
