package org.example.homestylebe.dto.response;

import lombok.Data;
import java.util.UUID;

import org.example.homestylebe.entity.AreaCasa;

@Data
public class StanzaResponseDTO {

    private UUID id;
    private AreaCasa tipologia;

}
