package org.example.homestylebe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;
import org.example.homestylebe.dto.request.ResoRequestDTO;
import org.example.homestylebe.dto.response.ResoResponseDTO;
import org.example.homestylebe.entity.Reso;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResoMapper {
    ResoResponseDTO toDTO(Reso entity);
    List<ResoResponseDTO> toDTOs(List<Reso> entities);
    Reso toEntity(ResoRequestDTO request);
    List<Reso> toEntities(List<ResoRequestDTO> requests);

}
